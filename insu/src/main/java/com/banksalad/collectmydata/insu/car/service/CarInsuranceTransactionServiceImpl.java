package com.banksalad.collectmydata.insu.car.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceRequest;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceResponse;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsResponse;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarInsuranceTransactionServiceImpl implements CarInsuranceTransactionService {

  private final CollectExecutor collectExecutor;
  private static final String AUTHORIZATION = "Authorization";
  private static final int MAX_LIMIT = 2;

  @Override
  public List<CarInsuranceTransaction> listCarInsuranceTransactions(ExecutionContext executionContext,
      String organizationCode, List<CarInsurance> carInsurances) {

    // TODO : private api 호출 메서드 로직 정상적으로 동작하는지 테스트 검증
    return null;
  }

  private ListCarInsuranceTransactionsResponse listCarInsuranceTransactions(ExecutionContext executionContext,
      Organization organization, InsuranceSummary insuranceSummary, CarInsurance carInsurance, String fromDate,
      String toDate) {

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    ListCarInsuranceTransactionsResponse response = ListCarInsuranceTransactionsResponse.builder().build();
    ListCarInsuranceTransactionsRequest request = ListCarInsuranceTransactionsRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .insuNum(insuranceSummary.getInsuNum())
        .carNumber(carInsurance.getCarNumber())
        .fromDate(fromDate)
        .toDate(toDate)
        .limit(MAX_LIMIT)
        .build();
    do {
      ExecutionRequest<GetCarInsuranceRequest> executionRequest = ExecutionUtil
          .assembleExecutionRequest(headers, request);
      ExecutionResponse<ListCarInsuranceTransactionsResponse> executionResponse = collectExecutor
          .execute(executionContext, Executions.insurance_get_car_transactions, executionRequest);

      // TODO : supposed to be in common error-catch logic
      if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        throw new CollectRuntimeException("execution statue code is not a 200 OK");
      }
      if (executionResponse.getResponse() == null) {
        throw new CollectRuntimeException("response is null");
      }

      ListCarInsuranceTransactionsResponse pageResponse = executionResponse.getResponse();
      response.setRspCode(pageResponse.getRspCode());
      response.setRspMsg(pageResponse.getRspMsg());
      response.setNextPage(pageResponse.getNextPage());
      response.setTransCnt(response.getTransCnt() + pageResponse.getTransCnt());
      response.getCarInsuranceTransactions().addAll(pageResponse.getCarInsuranceTransactions());

      request.setNextPage(pageResponse.getNextPage());
    } while (response.getNextPage() != null);

    if (response.getTransCnt() != response.getCarInsuranceTransactions().size()) {
      log.error("transactions size not equal. cnt: {}, size: {}", response.getTransCnt(),
          response.getCarInsuranceTransactions().size());
    }
    return response;
  }
}
