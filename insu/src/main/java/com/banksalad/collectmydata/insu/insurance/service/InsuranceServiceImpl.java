package com.banksalad.collectmydata.insu.insurance.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceContractRequest;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceContractResponse;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicRequest;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicResponse;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {

  private final CollectExecutor collectExecutor;
  private static final String AUTHORIZATION = "Authorization";

  @Override
  public List<InsuranceBasic> listInsuranceBasics(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries) {
    return null;
  }

  @Override
  public List<InsuranceContract> listInsuranceContracts(ExecutionContext executionContext, String organizationCode,
      List<InsuranceBasic> insuranceBasics) {
    return null;
  }
  
  private GetInsuranceBasicResponse GetInsuranceBasic(ExecutionContext executionContext, String orgCode, String insuNum,
      long searchTimestamp) {
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    GetInsuranceBasicRequest request = GetInsuranceBasicRequest.builder()
        .orgCode(orgCode)
        .insuNum(insuNum)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<GetInsuranceBasicRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<GetInsuranceBasicResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_basic, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }
  
   // 피보험자순번(insuredNo) 을 받는 명세에서는 String인데 Request에서는 int로 되어있는상태. 그러나 N(2)로 되어있어서 String이 되지않을까 추측합니다.
  private GetInsuranceContractResponse getInsuranceContract(ExecutionContext executionContext, String orgCode,
      String insuNum, String insuredNo, long searchTimestamp) {
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    GetInsuranceContractRequest request = GetInsuranceContractRequest.builder()
        .orgCode(orgCode)
        .insuNum(insuNum)
        .insuredNo(insuredNo)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<GetInsuranceContractRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<GetInsuranceContractResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_contract, executionRequest);
  
    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }
}
