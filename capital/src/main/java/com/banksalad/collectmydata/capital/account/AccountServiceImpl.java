package com.banksalad.collectmydata.capital.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ExternalApiService externalApiService;
  private final AccountListRepository accountListRepository;

  @Override

  public void syncAllAccounts(ExecutionContext executionContext, Organization organization) {
    externalApiService.getAccounts(executionContext, organization);

    // TODO ...
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
