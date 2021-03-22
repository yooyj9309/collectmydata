package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.account.dto.ListAccountTransactionsResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountTransactionInterestMapper;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountTransactionMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionInterestRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class AccountTransactionResponseHelper implements TransactionResponseHelper<AccountSummary, AccountTransaction> {

  private final AccountSummaryService accountSummaryService;
  private final AccountTransactionRepository accountTransactionRepository;
  private final AccountTransactionInterestRepository accountTransactionInterestRepository;
  private final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);
  private final AccountTransactionInterestMapper accountTransactionInterestMapper = Mappers
      .getMapper(AccountTransactionInterestMapper.class);


  @Override
  public List<AccountTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListAccountTransactionsResponse) transactionResponse).getTransList();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<AccountTransaction> accountTransactions) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    String accountNum = accountSummary.getAccountNum();
    String seqno = accountSummary.getSeqno();

    for (AccountTransaction accountTransaction : accountTransactions) {
      final Integer transactionYearMonth = Integer.valueOf(accountTransaction.getTransDtime().substring(0, 6));
      final String uniqueTransNo = HashUtil.hashCat(accountTransaction.getTransDtime(),
          accountTransaction.getTransNo(), accountTransaction.getBalanceAmt().toString());

      // dto -> entity 생성
      AccountTransactionEntity accountTransactionEntity = AccountTransactionEntity.builder()
          .transactionYearMonth(transactionYearMonth)
          .syncedAt(executionContext.getSyncStartedAt())
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .accountNum(accountNum)
          .seqno(seqno)
          .uniqueTransNo(uniqueTransNo)
          .transDtime(accountTransaction.getTransDtime())
          .transNo(accountTransaction.getTransNo())
          .transType(accountTransaction.getTransType())
          .transAmt(accountTransaction.getTransAmt())
          .balanceAmt(accountTransaction.getBalanceAmt())
          .principalAmt(accountTransaction.getPrincipalAmt())
          .intAmt(accountTransaction.getIntAmt())
          .build();

      // entity 조회
      AccountTransactionEntity existingAccountTransactionEntity = accountTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
              banksaladUserId, organizationId, accountNum, seqno, transactionYearMonth, uniqueTransNo)
          .orElse(null);

      //  set Id
      if (existingAccountTransactionEntity != null) {
        accountTransactionEntity.setId(existingAccountTransactionEntity.getId());
      }

      // compare
      if (ObjectComparator.isSame(accountTransactionEntity, existingAccountTransactionEntity, ENTITY_EXCLUDE_FIELD)) {
        continue;
      }

      // save
      accountTransactionRepository.save(accountTransactionEntity);

      accountTransactionInterestRepository
          .deleteAllByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
              banksaladUserId, organizationId, accountNum, seqno, transactionYearMonth, uniqueTransNo
          );
      accountTransactionInterestRepository.flush();

      AtomicInteger counter = new AtomicInteger();
      List<AccountTransactionInterestEntity> accountTransactionInterestEntities =
          accountTransaction.getIntList().stream()
              .map(accountTransactionInterest -> {
                AccountTransactionInterestEntity accountTransactionInterestEntity =
                    accountTransactionInterestMapper
                        .toEntity(accountTransactionEntity, accountTransactionInterest);
                accountTransactionInterestEntity.setIntNo(counter.incrementAndGet());
                return accountTransactionInterestEntity;
              })
              .collect(Collectors.toList());
      accountTransactionInterestRepository.saveAll(accountTransactionInterestEntities);
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDateTime syncStartedAt) {
    accountSummaryService
        .updateTransactionSyncedAt(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary, syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService
        .updateTransactionResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary, responseCode);
  }

}
