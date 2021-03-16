package com.banksalad.collectmydata.referencebank.deposit;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.referencebank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.referencebank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.referencebank.common.enums.BankAccountType;
import com.banksalad.collectmydata.referencebank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.referencebank.deposit.dto.GetDepositAccountDetailRequest;
import com.banksalad.collectmydata.referencebank.summary.dto.AccountSummary;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DepositAccountDetailInfoRequestHelper implements
    AccountInfoRequestHelper<GetDepositAccountDetailRequest, AccountSummary> {

  private final AccountSummaryService accountSummaryService;
  private final AccountSummaryRepository accountSummaryRepository;

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {

    return accountSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            BankAccountType.DEPOSIT);
  }

  @Override
  public GetDepositAccountDetailRequest make(ExecutionContext executionContext, AccountSummary accountSummary) {
    long searchTimestamp = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno
            (executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), accountSummary.getAccountNum(),
                accountSummary.getSeqno())
        .map(AccountSummaryEntity::getDetailSearchTimestamp)
        .orElse(0L);

    return GetDepositAccountDetailRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .searchTimestamp(searchTimestamp)
        .build();
  }
}
