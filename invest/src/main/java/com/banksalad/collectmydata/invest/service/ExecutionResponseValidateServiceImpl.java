package com.banksalad.collectmydata.invest.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.invest.common.db.entity.ApiLogEntity;
import com.banksalad.collectmydata.invest.common.db.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.common.util.DateUtil.UTC_ZONE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutionResponseValidateServiceImpl implements ExecutionResponseValidateService {

  private final ApiLogRepository apiLogRepository;
  private static final String SUCCESS_RESULT_CODE = "00000";

  @Override
  public Boolean isAllResponseResultSuccess(ExecutionContext executionContext, Boolean isExceptionOccurred) {
    if (isExceptionOccurred) {
      return false;
    }
    List<ApiLogEntity> apiLogEntities = apiLogRepository.findBySyncRequestIdAndExecutionRequestIdAndCreatedAtBetween(
        executionContext.getSyncRequestId(),
        executionContext.getExecutionRequestId(),
        LocalDateTime.now(UTC_ZONE_ID).minusDays(1),
        LocalDateTime.now(UTC_ZONE_ID).plusDays(1)
    );

    // TODO 추후에 해당 부분 resultCode를 가지고 별도의 작업(로깅,모니터링 metric 또는 무언가..)을 하는경우, .Collect로 변경
    long resultCount = apiLogEntities.stream()
        .filter(apiLogEntity -> !SUCCESS_RESULT_CODE.equals(apiLogEntity.getResultCode()))
        .count();

    return resultCount == 0;
  }
}
