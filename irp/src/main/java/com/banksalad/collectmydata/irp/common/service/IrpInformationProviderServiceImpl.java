package com.banksalad.collectmydata.irp.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.ListIrpAccountSummariesRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummariesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class IrpInformationProviderServiceImpl implements IrpInformationProviderService {

  private static final String AUTHORIZATION = "Authorization";
  private final CollectExecutor collectExecutor;

  @Override
  public IrpAccountSummariesResponse getIrpAccountSummaries(ExecutionContext executionContext, String orgCode, long searchTimeStamp) {

    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    ListIrpAccountSummariesRequest irpAccountSummaryRequest = ListIrpAccountSummariesRequest.builder()
        .searchTimestamp(searchTimeStamp)
        .orgCode(orgCode)
        .build();

    ExecutionRequest<ListIrpAccountSummariesRequest> executionRequest = ExecutionRequest.<ListIrpAccountSummariesRequest>builder()
        .headers(headers)
        .request(irpAccountSummaryRequest)
        .build();

    return execute(executionContext, Executions.irp_get_accounts, executionRequest);
  }

  @Override
  public IrpAccountBasicResponse getAccountBasic(ExecutionContext executionContext, Organization organization,
      IrpAccountSummary irpAccountSummary) {

    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    IrpAccountBasicRequest irpAccountBasicRequest = IrpAccountBasicRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(irpAccountSummary.getAccountNum())
        .seqno(irpAccountSummary.getSeqno())
        .searchTimestamp(irpAccountSummary.getBasicSearchTimestamp())
        .build();

    ExecutionRequest<IrpAccountBasicRequest> executionRequest = ExecutionRequest.<IrpAccountBasicRequest>builder()
        .headers(headers)
        .request(irpAccountBasicRequest)
        .build();

    return execute(executionContext, Executions.irp_get_basic, executionRequest);
  }

  private <T, R> R execute(ExecutionContext executionContext, Execution execution,
      ExecutionRequest<T> executionRequest) {

    ExecutionResponse<R> executionResponse = collectExecutor.execute(executionContext, execution, executionRequest);

    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
      // TODO: logging, execution monitoring
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }
    return executionResponse.getResponse();
  }
}
