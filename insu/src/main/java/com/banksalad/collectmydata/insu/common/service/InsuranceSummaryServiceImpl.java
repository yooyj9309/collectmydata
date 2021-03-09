package com.banksalad.collectmydata.insu.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.common.dto.ListInsuranceSummariesRequest;
import com.banksalad.collectmydata.insu.common.dto.ListInsuranceSummariesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceSummaryServiceImpl implements InsuranceSummaryService {

  private final InsuranceSummaryRepository insuranceSummaryRepository;
  private final CollectExecutor collectExecutor;
  
  private static final String AUTHORIZATION = "Authorization";

  @Override
  public List<InsuranceSummary> listInsuranceSummaries(ExecutionContext executionContext, String organizationCode) {
    return null;
  }

  @Override
  public void updateSearchTimestamp(long banksaladUserId, String organizationId,
      InsuranceSummary insuranceSummary) {
    if (insuranceSummary == null) {
      throw new CollectRuntimeException("Invalid insurance");
    }

    InsuranceSummaryEntity entity = insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            banksaladUserId,
            organizationId,
            insuranceSummary.getInsuNum()
        ).orElseThrow(() -> new CollectRuntimeException("No data AccountSummaryEntity"));

    entity.setBasicSearchTimestamp(insuranceSummary.getBasicSearchTimestamp());
    entity.setCarSearchTimestamp(0L); //TODO
    entity.setPaymentSearchTimestamp(0L); //TODO
    entity.setCarInsuranceTransactionFromDate(LocalDate.now()); // TODO
    entity.setInsuranceTransactionFromDate(LocalDate.now()); // TODO
    insuranceSummaryRepository.save(entity);
  }
  
  private ListInsuranceSummariesResponse listInsuranceSummariesResponse(ExecutionContext executionContext,
      String organizationCode, long searchTimestamp) {

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    ListInsuranceSummariesRequest request = ListInsuranceSummariesRequest.builder()
        .orgCode(organizationCode)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<ListInsuranceSummariesRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<ListInsuranceSummariesResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_summaries, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }
}
