package com.banksalad.collectmydata.bank.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.enums.BankAccountType;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountDetailRequest;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanAccountDetailInfoRequestHelper implements
    AccountInfoRequestHelper<GetLoanAccountDetailRequest, AccountSummary> {

  private static final long DEFAULT_SEARCH_TIMESTAMP = 0L;

  private final AccountSummaryService accountSummaryService;
  private final AccountSummaryRepository accountSummaryRepository;

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    return accountSummaryService.listSummariesConsented(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        BankAccountType.LOAN
    );
  }

  @Override
  public GetLoanAccountDetailRequest make(ExecutionContext executionContext, AccountSummary accountSummary) {
    long searchTimestamp = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno())
        .map(AccountSummaryEntity::getDetailSearchTimestamp)
        .orElse(DEFAULT_SEARCH_TIMESTAMP);

    return GetLoanAccountDetailRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .searchTimestamp(String.valueOf(searchTimestamp))
        .build();
  }
}
