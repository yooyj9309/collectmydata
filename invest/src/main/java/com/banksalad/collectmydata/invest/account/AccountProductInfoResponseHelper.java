package com.banksalad.collectmydata.invest.account;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.invest.account.dto.AccountProduct;
import com.banksalad.collectmydata.invest.account.dto.ListAccountProductsResponse;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductHistoryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountProductHistoryMapper;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountProductMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountProductHistoryRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountProductRepository;
import com.banksalad.collectmydata.invest.common.service.AccountSummaryService;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountProductInfoResponseHelper implements
    AccountInfoResponseHelper<AccountSummary, List<AccountProduct>> {

  private final AccountSummaryService accountSummaryService;
  private final AccountProductRepository accountProductRepository;
  private final AccountProductHistoryRepository accountProductHistoryRepository;

  private final AccountProductMapper accountProductMapper = Mappers.getMapper(AccountProductMapper.class);
  private final AccountProductHistoryMapper accountProductHistoryMapper = Mappers.getMapper(AccountProductHistoryMapper.class);

  @Override
  public List<AccountProduct> getAccountFromResponse(AccountResponse accountResponse) {
    return ((ListAccountProductsResponse) accountResponse).getProducts();
  }

  @Override
  @Transactional
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      List<AccountProduct> accountProducts) {

    List<AccountProduct> existingAccountProducts = accountProductRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary.getAccountNum())
        .stream()
        .map(accountProductMapper::entityToDto)
        .collect(Collectors.toList());

    if (!ObjectComparator.isSameListIgnoreOrder(accountProducts, existingAccountProducts)) {
      accountProductRepository
          .deleteAllByBanksaladUserIdAndOrganizationIdAndAccountNum(executionContext.getBanksaladUserId(),
              executionContext.getOrganizationId(), accountSummary.getAccountNum());
      accountProductRepository.flush();

      short prodNo = 1;
      for (AccountProduct accountProduct : accountProducts) {
        AccountProductEntity accountProductEntity = accountProductMapper.dtoToEntity(accountProduct);
        accountProductEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
        accountProductEntity.setOrganizationId(executionContext.getOrganizationId());
        accountProductEntity.setSyncedAt(executionContext.getSyncStartedAt());
        accountProductEntity.setAccountNum(accountSummary.getAccountNum());
        accountProductEntity.setProdNo(prodNo++);
        accountProductEntity.setConsentId(executionContext.getConsentId());
        accountProductEntity.setSyncRequestId(executionContext.getSyncRequestId());
        accountProductEntity.setCreatedBy(executionContext.getRequestedBy());
        accountProductEntity.setUpdatedBy(executionContext.getRequestedBy());

        accountProductRepository.save(accountProductEntity);

        AccountProductHistoryEntity accountProductHistoryEntity = accountProductHistoryMapper.toHistoryEntity(accountProductEntity);
        accountProductHistoryEntity.setCreatedBy(executionContext.getRequestedBy());
        accountProductHistoryEntity.setUpdatedBy(executionContext.getRequestedBy());

        accountProductHistoryRepository.save(accountProductHistoryEntity);
      }
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {

    accountSummaryService
        .updateProductSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary.getAccountNum(), searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService
        .updateProductResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary.getAccountNum(), responseCode);
  }
}
