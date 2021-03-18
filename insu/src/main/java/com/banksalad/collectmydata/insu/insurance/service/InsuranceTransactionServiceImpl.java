package com.banksalad.collectmydata.insu.insurance.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceTransactionEntity;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceTransactionMapper;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceTransactionRepository;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceTransaction;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceTransactionServiceImpl implements InsuranceTransactionService {

  private final InsuranceSummaryService insuranceSummaryService;
  private final UserSyncStatusService userSyncStatusService;
  private final InsuranceTransactionRepository insuranceTransactionRepository;
  private final CollectExecutor collectExecutor;
  @Qualifier("async-thread")
  private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

  private final InsuranceTransactionMapper insuranceTransactionMapper = Mappers
      .getMapper(InsuranceTransactionMapper.class);
  private static final String AUTHORIZATION = "Authorization";
  private static final int LIMIT = 500;

  @Override
  public List<InsuranceTransaction> listInsuranceTransactions(ExecutionContext executionContext,
      String organizationCode, List<InsuranceSummary> insuranceSummaries) {
    AtomicReference<Boolean> isExceptionOccurred = new AtomicReference<>(false);

    List<InsuranceTransaction> insuranceTransactions = insuranceSummaries.stream()
        .map(insuranceSummary -> CompletableFuture
            .supplyAsync(() ->
                    progressInsuranceTransactions(executionContext, organizationCode, insuranceSummary),
                threadPoolTaskExecutor
            ).exceptionally(e -> {
              log.error("6.5.6 insuranceBasic exception {}", e.getMessage());
              isExceptionOccurred.set(true);
              return null;
            })
        ).map(CompletableFuture::join)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

//    userSyncStatusService
//        .updateUserSyncStatus(
//            executionContext.getBanksaladUserId(),
//            executionContext.getOrganizationId(),
//            Apis.insurance_get_transactions.getId(),
//            executionContext.getSyncStartedAt(),
//            null,
//            executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred.get())
//        );
    return insuranceTransactions;
  }

  private List<InsuranceTransaction> progressInsuranceTransactions(ExecutionContext executionContext, String orgCode,
      InsuranceSummary insuranceSummary) {
    String insuNum = insuranceSummary.getInsuNum();
    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();

    // utc 시간을 kst yyyymmdd로 변환.
    LocalDateTime transactionSyncedAt = Optional.ofNullable(insuranceSummary.getTransactionSyncedAt()).orElse(
        executionContext.getSyncStartedAt().minusYears(5L)
    );

    String fromDate = DateUtil.utcLocalDateTimeToKstDateString(transactionSyncedAt);
    String toDate = DateUtil.utcLocalDateTimeToKstDateString(executionContext.getSyncStartedAt());

    ListInsuranceTransactionsResponse transactionApiResponse = listInsuranceTransactions(executionContext, orgCode,
        insuNum, fromDate, toDate);

    for (InsuranceTransaction insuranceTransaction : transactionApiResponse.getTransList()) {
      insuranceTransaction.setInsuNum(insuNum);
      Integer transactionYearMonth = Integer.parseInt(insuranceTransaction.getTransAppliedMonth());

      // db조회
      // unique key banksalad_user_id,organization_id,insu_num,trans_no,transaction_year_month
      InsuranceTransactionEntity transactionEntity = insuranceTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndTransNoAndTransactionYearMonth(
              banksaladUserId,
              organizationId,
              insuNum,
              insuranceTransaction.getTransNo(),
              transactionYearMonth
          ).orElse(InsuranceTransactionEntity.builder().build());

      // merge
      insuranceTransactionMapper.merge(insuranceTransaction, transactionEntity);
      transactionEntity.setBanksaladUserId(banksaladUserId);
      transactionEntity.setOrganizationId(organizationId);
      transactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      transactionEntity.setTransactionYearMonth(transactionYearMonth);

      // save
      insuranceTransactionRepository.save(transactionEntity);

      insuranceSummaryService
          .updateTransactionSyncedAt(banksaladUserId, organizationId, insuNum, executionContext.getSyncStartedAt());
    }
    return transactionApiResponse.getTransList();
  }


  private ListInsuranceTransactionsResponse listInsuranceTransactions(ExecutionContext executionContext, String orgCode,
      String insuNum, String fromDate, String toDate) {
    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    ListInsuranceTransactionsRequest request = ListInsuranceTransactionsRequest.builder()
        .orgCode(orgCode)
        .insuNum(insuNum)
        .fromDate(fromDate)
        .toDate(toDate)
        .limit(LIMIT)
        .build();

    ListInsuranceTransactionsResponse response = ListInsuranceTransactionsResponse.builder().build();
    List<InsuranceTransaction> responseInsuranceTransactionList = new ArrayList<>();
    int responseTransCnt = 0;
    do {
      ExecutionRequest<ListInsuranceTransactionsRequest> executionRequest = ExecutionUtil
          .assembleExecutionRequest(headers, request);

      ExecutionResponse<ListInsuranceTransactionsResponse> executionResponse = collectExecutor
          .execute(executionContext, Executions.insurance_get_transactions, executionRequest);

      if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        throw new CollectRuntimeException("execution Statue is not OK");
      }

      if (executionResponse.getResponse() == null) {
        throw new CollectRuntimeException("response is null");
      }

      ListInsuranceTransactionsResponse page = executionResponse.getResponse();
      if (page.getTransCnt() != page.getTransList().size()) {
        log.info("The transaction count is different, organizationId: {},", executionContext.getOrganizationId());
      }

      response.setRspCode(page.getRspCode());
      responseTransCnt += page.getTransCnt();
      responseInsuranceTransactionList.addAll(page.getTransList());

      request.setNextPage(page.getNextPage());
    } while (request.getNextPage() != null);

    response.setTransCnt(responseTransCnt);
    response.setTransList(responseInsuranceTransactionList);

    return response;
  }
}
