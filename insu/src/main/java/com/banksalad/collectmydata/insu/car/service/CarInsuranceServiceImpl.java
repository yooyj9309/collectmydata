package com.banksalad.collectmydata.insu.car.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceRequest;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceResponse;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarInsuranceServiceImpl implements CarInsuranceService {

  private final CollectExecutor collectExecutor;
  private static final String AUTHORIZATION = "Authorization";

  @Override
  public List<CarInsurance> listCarInsurances(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries) {
    return null;
  }

  private GetCarInsuranceResponse listCarInsurances(ExecutionContext executionContext, Organization organization,
      InsuranceSummary insuranceSummary) {

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    GetCarInsuranceRequest request = GetCarInsuranceRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .insuNum(insuranceSummary.getInsuNum())
        .serachTimestamp(0L) // TODO : get searchTimestamp from account_summary table
        .build();

    ExecutionRequest<GetCarInsuranceRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<GetCarInsuranceResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_car, executionRequest);

    // TODO : supposed to be in common error-catch logic
    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution statue code is not a 200 OK");
    }
    if (executionResponse == null || executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }
    return executionResponse.getResponse();
  }
}
