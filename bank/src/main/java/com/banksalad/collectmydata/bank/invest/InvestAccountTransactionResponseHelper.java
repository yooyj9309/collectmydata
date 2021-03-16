package com.banksalad.collectmydata.bank.invest;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.InvestAccountTransactionMapper;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.bank.invest.dto.ListInvestAccountTransactionsResponse;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class InvestAccountTransactionResponseHelper implements
    TransactionResponseHelper<AccountSummary, InvestAccountTransaction> {

  private final AccountSummaryService accountSummaryService;
  private final InvestAccountTransactionRepository investAccountTransactionRepository;
  private final InvestAccountTransactionMapper investAccountTransactionMapper = Mappers
      .getMapper(InvestAccountTransactionMapper.class);

  @Override
  public List<InvestAccountTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    ListInvestAccountTransactionsResponse response = (ListInvestAccountTransactionsResponse) transactionResponse;
    return response.getInvestAccountTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<InvestAccountTransaction> investAccountTransactions) {
    for (InvestAccountTransaction investAccountTransaction : investAccountTransactions) {
      InvestAccountTransactionEntity investAccountTransactionEntity = investAccountTransactionMapper
          .dtoToEntity(investAccountTransaction);
      investAccountTransactionEntity.setTransactionYearMonth(generateTransactionYearMonth(investAccountTransaction));
      investAccountTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      investAccountTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      investAccountTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      investAccountTransactionEntity.setAccountNum(accountSummary.getAccountNum());
      investAccountTransactionEntity.setSeqno(accountSummary.getSeqno());
      investAccountTransactionEntity.setUniqueTransNo(generateUniqueTransNo(investAccountTransaction));

      InvestAccountTransactionEntity existingInvestAccountTransactionEntity = investAccountTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUniqueTransNoAndTransactionYearMonth(
              investAccountTransactionEntity.getBanksaladUserId(),
              investAccountTransactionEntity.getOrganizationId(),
              investAccountTransactionEntity.getAccountNum(),
              investAccountTransactionEntity.getSeqno(),
              investAccountTransactionEntity.getUniqueTransNo(),
              investAccountTransactionEntity.getTransactionYearMonth()
          );

      if (existingInvestAccountTransactionEntity != null) {
        investAccountTransactionEntity.setId(existingInvestAccountTransactionEntity.getId());
      }

      if (!ObjectComparator
          .isSame(investAccountTransactionEntity, existingInvestAccountTransactionEntity, ENTITY_EXCLUDE_FIELD)) {
        investAccountTransactionRepository.save(investAccountTransactionEntity);
      }
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDateTime syncStartedAt) {
    accountSummaryService.updateTransactionSyncedAt(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        syncStartedAt
    );
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService.updateTransactionResponseCode(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        responseCode
    );
  }

  private int generateTransactionYearMonth(InvestAccountTransaction depositAccountTransaction) {
    String transDtime = depositAccountTransaction.getTransDtime();
    String yearMonthString = transDtime.substring(0, 6);

    return Integer.valueOf(yearMonthString);
  }

  private String generateUniqueTransNo(InvestAccountTransaction depositAccountTransaction) {
    String transDtime = depositAccountTransaction.getTransDtime();
    String transType = depositAccountTransaction.getTransType();
    String transAmtString = depositAccountTransaction.getTransAmt().toString();
    String balanceAmtString = depositAccountTransaction.getBalanceAmt().toString();

    return HashUtil.hashCat(transDtime, transType, transAmtString, balanceAmtString);
  }

}