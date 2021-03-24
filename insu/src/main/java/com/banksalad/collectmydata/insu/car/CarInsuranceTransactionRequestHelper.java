package com.banksalad.collectmydata.insu.car;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.car.service.CarInsuranceService;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.*;

@Component
@RequiredArgsConstructor
public class CarInsuranceTransactionRequestHelper implements
    TransactionRequestHelper<CarInsurance, ListCarInsuranceTransactionsRequest> {

  private final CarInsuranceService carInsuranceService;
  private final InsuranceSummaryService insuranceSummaryService;

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final int DEFAULT_PAGING_LIMIT = 100;

  @Override
  public List<CarInsurance> listSummaries(ExecutionContext executionContext) {

    /* load insurance summary to get insuNum */
    List<InsuranceSummary> insuranceSummaries = insuranceSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());

    /* load all car insurance list by banksaladUserId & organizationId */
    return insuranceSummaries.stream()
        .map(insuranceSummary -> carInsuranceService.listCarInsurances(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), insuranceSummary.getInsuNum()))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, CarInsurance carInsurance) {
    LocalDateTime transactionSyncedAt = carInsurance.getTransactionSyncedAt();
    if (transactionSyncedAt == null) {
      return executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR);
    }
    return transactionSyncedAt;
  }

  @Override
  public ListCarInsuranceTransactionsRequest make(ExecutionContext executionContext, CarInsurance carInsurance,
      LocalDate fromDate, LocalDate toDate, String nextPage) {
    return ListCarInsuranceTransactionsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .insuNum(carInsurance.getInsuNum())
        .carNumber(carInsurance.getCarNumber())
        .fromDate(dateFormatter.format(fromDate))
        .toDate(dateFormatter.format(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_PAGING_LIMIT)
        .build();
  }
}
