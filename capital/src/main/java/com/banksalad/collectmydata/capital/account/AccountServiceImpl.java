package com.banksalad.collectmydata.capital.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.account.dto.AccountInfo;
import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ExternalApiService externalApiService;
  private final AccountListRepository accountListRepository;

  /**
   * 6.7.1 계좌 목록 조회
   */
  @Override
  public List<Account> listAccounts(ExecutionContext executionContext, Organization organization) {
    externalApiService.getAccounts(executionContext, organization);

    // TODO ...
    List<Account> accounts = null;
    return accounts;
  }

  /**
   * on-demand 6.7.2 (대출상품계좌 기본정보 조회) 및 6.7.3(대출상품계좌 추가정보 조회) 두개를 조회하여 조합, 적재
   *
   * @param executionContext
   * @param organization
   * @param accounts
   * @return List<AccountInfo>
   */
  @Override
  public List<AccountInfo> listAccountInfo(ExecutionContext executionContext, Organization organization,
      List<Account> accounts) {

    // 2번 3번 api 조합

    return null;
  }

  /**
   * 정기전송 시점에 6.7.2만 호출되는 경우. 업데이트가 있는경우 List<AccountInfo>에 매핑
   */
  @Override
  public List<AccountInfo> listAccountBasicInfo(ExecutionContext executionContext, Organization organization,
      List<Account> accounts) {
    return null;
  }

  /**
   * 정기전송 시점에 6.7.3만 호출되는 경우. 업데이트가 있는경우 List<AccountInfo>에 매핑
   */
  @Override
  public List<AccountInfo> listAccountDetailInfo(ExecutionContext executionContext, Organization organization,
      List<Account> accounts) {
    return null;
  }

  /**
   * 6.7.4 대출상품계좌 거래내역 조회
   *
   * @param executionContext
   * @param organization
   * @param accounts
   * @return
   */
  @Override
  public List<AccountTransaction> listAccountTransaction(ExecutionContext executionContext, Organization organization,
      List<Account> accounts) {
    return null;
  }

  @Override
  public void updateSearchTimestampForAccount(long banksaladUserId, String organizationId, Account account) {
    if (account == null) {
      throw new CollectRuntimeException("Invalid account"); //TODO
    }

    AccountListEntity entity = accountListRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId,
            organizationId,
            account.getAccountNum(),
            account.getSeqno()
        ).orElseThrow(() -> new CollectRuntimeException("No data AccountListEntity")); //TODO

    entity.setBasicSearchTimestamp(account.getBasicSearchTimestamp());
    entity.setDetailSearchTimestamp(account.getDetailSearchTimestamp());
    entity.setOperatingLeaseBasicSearchTimestamp(account.getOperatingLeaseBasicSearchTimestamp());
    accountListRepository.save(entity);
  }
}
