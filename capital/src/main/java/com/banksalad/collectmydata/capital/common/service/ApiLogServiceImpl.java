package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.db.entity.ApiLogEntity;
import com.banksalad.collectmydata.capital.common.db.repository.ApiLogRepository;
import com.banksalad.collectmydata.common.collect.executor.ApiLog;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.banksalad.collectmydata.common.util.DateUtil.UTC_ZONE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiLogServiceImpl implements ApiLogService {

  private final ApiLogRepository apiLogRepository;

  @Override
  public void logRequest(String syncRequestId, String executionRequestId, long banksaladUserId, String organizationId,
      ApiLog apiLog) {
    apiLogRepository.save(
        ApiLogEntity.builder()
            .syncRequestId(syncRequestId)
            .executionRequestId(executionRequestId)
            .apiRequestId(apiLog.getId())
            .organizationId(organizationId)
            .banksaladUserId(banksaladUserId)
            .apiId(apiLog.getApi().getId())
            .organizationApiId(apiLog.getApi().getName())
            .requestUrl(apiLog.getApi().getEndpoint())
            .httpMethod(apiLog.getApi().getMethod())
            .requestHeader(apiLog.getRequest().getHeader())
            .requestBody(apiLog.getRequest().getBody())
            .transformedRequestHeader(apiLog.getRequest().getTransformedHeader())
            .transformedRequestBody(apiLog.getRequest().getTransformedBody())
            .requestDtime(LocalDateTime.now())
            .build()
    );
  }

  @Override
  public void logResponse(String syncRequestId, String executionRequestId, long banksaladUserId, String organizationId,
      ApiLog apiLog) {

    Result result = parseResultCodeAndMessage(apiLog.getResponse().getTransformedBody());

    ApiLogEntity apiLogEntity = apiLogRepository
        .findBySyncRequestIdAndExecutionRequestIdAndApiRequestIdAndCreatedAtBetween(
            syncRequestId,
            executionRequestId,
            apiLog.getId(),
            LocalDateTime.now(UTC_ZONE_ID).minusDays(1),
            LocalDateTime.now(UTC_ZONE_ID).plusDays(1)
        ).orElseGet(() ->
            ApiLogEntity.builder()
                .syncRequestId(syncRequestId)
                .executionRequestId(executionRequestId)
                .apiRequestId(apiLog.getId())
                .apiId(apiLog.getApi().getId())
                .organizationApiId(apiLog.getApi().getName())
                .requestUrl(apiLog.getApi().getEndpoint())
                .httpMethod(apiLog.getApi().getMethod())
                .build()
        );

    apiLogEntity.setResultCode(result.resultCode);
    apiLogEntity.setResultMessage(result.resultMessage);
    apiLogEntity.setResponseCode(apiLog.getResponse().getResponseCode());
    apiLogEntity.setResponseHeader(apiLog.getResponse().getHeader());
    apiLogEntity.setResponseBody(apiLog.getResponse().getBody());
    apiLogEntity.setTransformedResponseHeader(apiLog.getResponse().getTransformedHeader());
    apiLogEntity.setTransformedResponseBody(apiLog.getResponse().getTransformedBody());
    apiLogEntity.setResponseDtime(LocalDateTime.now());

    if (apiLogEntity.getRequestDtime() != null) {
      long elapsedTime = DateUtil.utcLocalDateTimeToEpochMilliSecond(apiLogEntity.getResponseDtime()) - DateUtil
          .utcLocalDateTimeToEpochMilliSecond(apiLogEntity.getRequestDtime());
      apiLogEntity.setElapsedTime(elapsedTime);
    }

    apiLogRepository.save(apiLogEntity);
  }

  // 할부업권엔 error, error_description 가 없는 상태로, rsp_code,rsp_msg 로 통일
  private Result parseResultCodeAndMessage(String json) {
    String resultCode = null;
    String resultMessage = null;

    try {
      resultCode = JsonPath.parse(json).read("$.rsp_code", String.class);
    } catch (PathNotFoundException ignore) {
    }

    try {
      resultMessage = JsonPath.parse(json).read("$.rsp_msg", String.class);
    } catch (PathNotFoundException ignore) {
    }

    return Result.builder()
        .resultCode(resultCode)
        .resultMessage(resultMessage)
        .build();
  }

  @Getter
  @Builder
  private static class Result {

    private String resultCode;
    private String resultMessage;
  }
}
