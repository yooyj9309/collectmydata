package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.exception.CollectmydataRuntimeException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountSummaryServiceImpl implements AccountSummaryService {
  
  private final AccountSummaryRepository accountSummaryRepository;

  @Override
  public void updateSearchTimestamp(long banksaladUserId, String organizationId,
      AccountSummary accountSummary) {
    if (accountSummary == null) {
      throw new CollectRuntimeException("Invalid account"); //TODO
    }

    AccountSummaryEntity entity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId,
            organizationId,
            accountSummary.getAccountNum(),
            accountSummary.getSeqno()
        ).orElseThrow(() -> new CollectRuntimeException("No data AccountSummaryEntity")); //TODO

    entity.setBasicSearchTimestamp(accountSummary.getBasicSearchTimestamp());
    entity.setDetailSearchTimestamp(accountSummary.getDetailSearchTimestamp());
    entity.setOperatingLeaseBasicSearchTimestamp(accountSummary.getOperatingLeaseBasicSearchTimestamp());
    accountSummaryRepository.save(entity);
  }

  @Override
  public void updateTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary) {
    // Make an entity by reading a record from DB.
    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String accountNum = accountSummary.getAccountNum();
    final String seqno = accountSummary.getSeqno();

    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountNum, seqno
        ).orElseThrow(() -> new CollectmydataRuntimeException(String.format(
            "No record in account_summary table: banksaladUserId=%s, organizationId=%s, accoutNum=%s, seqno=%s",
            banksaladUserId, organizationId, accountNum, seqno)));
    // Set the target field.
    accountSummaryEntity.setTransactionSyncedAt(executionContext.getSyncStartedAt());
    // Save the modified entity on DB.
    accountSummaryRepository.save(accountSummaryEntity);
  }

  @Override
  public void updateOperatingLeaseTransactionSyncedAt(ExecutionContext executionContext,
      AccountSummary accountSummary) {
    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElseThrow(() -> new CollectRuntimeException("Couldn't find AccountSummaryEntity"));

    accountSummaryEntity.setOperatingLeaseTransactionSyncedAt(executionContext.getSyncStartedAt());
    accountSummaryRepository.save(accountSummaryEntity);
  }
}
