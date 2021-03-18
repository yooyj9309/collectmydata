package com.banksalad.collectmydata.insu.loan.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionInterestEntity;
import com.banksalad.collectmydata.insu.common.mapper.LoanTransactionInterestMapper;
import com.banksalad.collectmydata.insu.common.mapper.LoanTransactionMapper;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionInterestRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionRepository;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import com.banksalad.collectmydata.insu.common.service.LoanSummaryService;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionRequest;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransactionInterest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanTransactionServiceImpl implements LoanTransactionService {

  private final CollectExecutor collectExecutor;
  private final LoanSummaryService loanSummaryService;
  private final UserSyncStatusService userSyncStatusService;
  private final LoanTransactionRepository loanTransactionRepository;
  private final LoanTransactionInterestRepository loanTransactionInterestRepository;

  private final LoanTransactionMapper loanTransactionMapper = Mappers.getMapper(LoanTransactionMapper.class);
  private final LoanTransactionInterestMapper loanTransactionInterestMapper = Mappers
      .getMapper(LoanTransactionInterestMapper.class);

  private static final String[] EXCLUDE_FIELDS = {"syncedAt", "createdAt", "updatedAt", "createdBy", "updatedBy"};
  private static final int INITIAL_YEARS_AGO = 5;

  @Override
  public List<LoanTransaction> listLoanTransactions(ExecutionContext executionContext, Organization organization,
      List<LoanSummary> loanSummaries) {
    List<LoanTransaction> loanTransactions = new ArrayList<>();

    boolean isExceptionOccurred = FALSE;
    for (LoanSummary loanSummary : loanSummaries) {
      try {
        ListLoanTransactionResponse response = getLoanTransactionResponse(executionContext, organization, loanSummary);
        saveLoanTransactionsWithInterests(executionContext, loanSummary, response);
        loanTransactions.addAll(response.getTransList());

        loanSummaryService
            .updateTransactionSyncedAt(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
                loanSummary.getAccountNum(), executionContext.getSyncStartedAt());
      } catch (Exception e) {
        isExceptionOccurred = TRUE;
        log.error("Failed to save loan transaction", e);
      }
    }

//    userSyncStatusService
//        .updateUserSyncStatus(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
//            Apis.insurance_get_loan_transactions.getId(), executionContext.getSyncStartedAt(), null,
//            executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred));

    return loanTransactions;
  }

  private void saveLoanTransactionsWithInterests(ExecutionContext executionContext,
      LoanSummary loanSummary, ListLoanTransactionResponse loanTransactionResponse) {
    for (LoanTransaction loanTransaction : loanTransactionResponse.getTransList()) {
      Integer transactionYearMonth = getTransactionYearMonthFrom(loanTransaction);

      LoanTransactionEntity loanTransactionEntityFromResponse = loanTransactionMapper
          .toLoanTransactionEntityFrom(loanTransaction);
      loanTransactionEntityFromResponse.setTransactionYearMonth(transactionYearMonth);
      loanTransactionEntityFromResponse.setSyncedAt(executionContext.getSyncStartedAt());
      loanTransactionEntityFromResponse.setBanksaladUserId(executionContext.getBanksaladUserId());
      loanTransactionEntityFromResponse.setOrganizationId(executionContext.getOrganizationId());
      loanTransactionEntityFromResponse.setAccountNum(loanSummary.getAccountNum());
      loanTransactionEntityFromResponse.setAccountType(loanSummary.getAccountType());

      LoanTransactionEntity loanTransactionEntityFromTable = loanTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeAndTransNoAndTransactionYearMonth(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), loanSummary.getAccountNum(),
              loanTransaction.getTransDtime(), loanTransaction.getTransNo(), transactionYearMonth)
          .orElse(LoanTransactionEntity.builder().build());

      if (loanTransactionEntityFromTable.getId() != null) {
        loanTransactionEntityFromResponse.setId(loanTransactionEntityFromTable.getId());
      }

      if (!ObjectComparator.isSame(loanTransactionEntityFromResponse, loanTransactionEntityFromTable, EXCLUDE_FIELDS)) {
        loanTransactionRepository.save(loanTransactionEntityFromResponse);
        saveInterestsOfTransaction(executionContext, loanSummary, loanTransaction, transactionYearMonth);
      }
    }
  }

  private void saveInterestsOfTransaction(ExecutionContext executionContext, LoanSummary loanSummary,
      LoanTransaction loanTransaction, Integer transactionYearMonth) {
    loanTransactionInterestRepository
        .deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeAndTransNoAndTransactionYearMonth(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            loanSummary.getAccountNum(), loanTransaction.getTransDtime(), loanTransaction.getTransNo(),
            Integer.valueOf(loanTransaction.getTransDtime().substring(0, 6)));

    int intNo = 1;
    for (LoanTransactionInterest loanTransactionInterest : loanTransaction.getIntList()) {
      LoanTransactionInterestEntity loanTransactionInterestEntity = loanTransactionInterestMapper
          .toLoanTransactionInterestEntityFrom(loanTransactionInterest);
      loanTransactionInterestEntity.setTransactionYearMonth(transactionYearMonth);
      loanTransactionInterestEntity.setSyncedAt(executionContext.getSyncStartedAt());
      loanTransactionInterestEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      loanTransactionInterestEntity.setOrganizationId(executionContext.getOrganizationId());
      loanTransactionInterestEntity.setAccountNum(loanSummary.getAccountNum());
      loanTransactionInterestEntity.setTransDtime(loanTransaction.getTransDtime());
      loanTransactionInterestEntity.setTransNo(loanTransaction.getTransNo());
      loanTransactionInterestEntity.setIntNo(intNo++);

      loanTransactionInterestRepository.save(loanTransactionInterestEntity);
    }
  }

  private Integer getTransactionYearMonthFrom(LoanTransaction loanTransaction) {
    return Integer.valueOf(loanTransaction.getTransDtime().substring(0, 6));
  }

  private ListLoanTransactionResponse getLoanTransactionResponse(ExecutionContext executionContext,
      Organization organization, LoanSummary loanSummary) {

    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> header = Map.of("Authorization", executionContext.getAccessToken());
    ListLoanTransactionRequest loanTransactionRequest = ListLoanTransactionRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(loanSummary.getAccountNum())
        .fromDate(loanSummary.getTransactionSyncedAt() == null ?
            DateUtil.utcLocalDateTimeToKstDateString(getInitialFromDate(executionContext)) :
            DateUtil.utcLocalDateTimeToKstDateString(loanSummary.getTransactionSyncedAt()))
        .toDate(DateUtil.utcLocalDateTimeToKstDateString(executionContext.getSyncStartedAt()))
        .limit(10)
        .build();

    ExecutionRequest<ListLoanTransactionRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(header, loanTransactionRequest);

    ListLoanTransactionResponse responseResult = ListLoanTransactionResponse.builder().build();
    do {
      ExecutionResponse<ListLoanTransactionResponse> executionResponse = collectExecutor
          .execute(executionContext, Executions.insurance_get_loan_transactions, executionRequest);
      ListLoanTransactionResponse page = executionResponse.getResponse();

      responseResult.setRspCode(page.getRspCode());
      responseResult.setRspMsg(page.getRspMsg());
      responseResult.setNextPage(page.getNextPage());
      responseResult.setTransCnt(responseResult.getTransCnt() + page.getTransCnt());
      responseResult.getTransList().addAll(page.getTransList());

      executionRequest.getRequest().setNextPage(page.getNextPage());
    } while (executionRequest.getRequest().getNextPage() != null);

    return responseResult;
  }

  private LocalDateTime getInitialFromDate(ExecutionContext executionContext) {
    return executionContext.getSyncStartedAt()
        .minusYears(INITIAL_YEARS_AGO)
        .plusDays(1L);
  }
}
