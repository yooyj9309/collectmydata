package com.banksalad.collectmydata.capital.loan;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccount;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanAccountServiceImpl implements LoanAccountService {

  private final ExternalApiService externalApiService;
  private final AccountListRepository accountListRepository;

  /**
   * on-demand 6.7.2 (대출상품계좌 기본정보 조회) 및 6.7.3(대출상품계좌 추가정보 조회) 두개를 조회하여 조합, 적재
   *
   * @param executionContext
   * @param organization
   * @param accountSummaries
   * @return List<AccountInfo>
   */
  @Override
  public List<LoanAccount> listLoanAccounts(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {

    // 2번 3번 api 조합

    return null;
  }

  /**
   * 정기전송 시점에 6.7.2만 호출되는 경우. 업데이트가 있는경우 List<AccountInfo>에 매핑
   */
  @Override
  public List<LoanAccount> listLoanAccountBasics(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {
    return null;
  }

  /**
   * 정기전송 시점에 6.7.3만 호출되는 경우. 업데이트가 있는경우 List<AccountInfo>에 매핑
   */
  @Override
  public List<LoanAccount> listLoanAccountDetails(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {
    return null;
  }

  /**
   * 6.7.4 대출상품계좌 거래내역 조회
   *
   * @param executionContext
   * @param organization
   * @param accountSummaries
   * @return
   */
  @Override
  public List<LoanAccountTransaction> listAccountTransactions(ExecutionContext executionContext,
      Organization organization,
      List<AccountSummary> accountSummaries) {
    return null;
  }

  @Override
  public void updateSearchTimestampOnAccount(long banksaladUserId, String organizationId,
      AccountSummary accountSummary) {
    if (accountSummary == null) {
      throw new CollectRuntimeException("Invalid account"); //TODO
    }

    AccountListEntity entity = accountListRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId,
            organizationId,
            accountSummary.getAccountNum(),
            accountSummary.getSeqno()
        ).orElseThrow(() -> new CollectRuntimeException("No data AccountListEntity")); //TODO

    entity.setBasicSearchTimestamp(accountSummary.getBasicSearchTimestamp());
    entity.setDetailSearchTimestamp(accountSummary.getDetailSearchTimestamp());
    entity.setOperatingLeaseBasicSearchTimestamp(accountSummary.getOperatingLeaseBasicSearchTimestamp());
    accountListRepository.save(entity);
  }
}
