package com.banksalad.collectmydata.bank.depoist;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.DepositAccountBasicHistoryMapper;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.DepositAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountBasicHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.common.service.ExternalApiService;
import com.banksalad.collectmydata.bank.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.bank.depoist.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.depoist.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.depoist.dto.GetDepositAccountBasicResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class DepositAccountServiceImpl implements DepositAccountService {

  private final AccountSummaryService accountSummaryService;
  private final UserSyncStatusService userSyncStatusService;
  private final ExternalApiService externalApiService;
  private final DepositAccountBasicRepository depositAccountBasicRepository;
  private final DepositAccountBasicHistoryRepository depositAccountBasicHistoryRepository;

  private final DepositAccountBasicMapper depositAccountBasicMapper = Mappers
      .getMapper(DepositAccountBasicMapper.class);

  private final DepositAccountBasicHistoryMapper depositAccountBasicHistoryMapper = Mappers
      .getMapper(DepositAccountBasicHistoryMapper.class);

  @Override
  @Transactional
  public List<DepositAccountBasic> listDepositAccountBasics(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries) {

    Organization organization = getOrganization(executionContext);

    for (AccountSummary accountSummary : accountSummaries) {
      GetDepositAccountBasicResponse depositAccountBasicResponse = externalApiService.getDepositAccountBasic(
          executionContext, organization.getOrganizationCode(), accountSummary.getAccountNum(),
          accountSummary.getSeqno(), accountSummary.getBasicSearchTimestamp());

      DepositAccountBasic depositAccountBasic = depositAccountBasicResponse.getDepositAccountBasic();

      try {
        accountSummaryService.updateBasicTimestamp(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary, depositAccountBasic.getSearchTimestamp());

        saveDepositAccountBasic(executionContext, accountSummary, depositAccountBasic);
      } catch (Exception e) {
        log.error("Failed to save deposit account basic", e);
      }
    }

    // Api 200 Ok, userSyncStatusService.upsert(executionContext);

    List<DepositAccountBasicEntity> depositAccountBasicEntities = depositAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId());

    return depositAccountBasicEntities.stream()
        .map(depositAccountBasicMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<DepositAccountDetail> listDepositAccountDetails(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries) {

    // TODO jayden-lee 수신계좌 상세정보 조회
    return Collections.emptyList();
  }

  private void saveDepositAccountBasic(ExecutionContext executionContext, AccountSummary accountSummary,
      DepositAccountBasic depositAccountBasic) {

    // convert to entity
    DepositAccountBasicEntity depositAccountBasicEntity = depositAccountBasicMapper.dtoToEntity(depositAccountBasic);
    depositAccountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    depositAccountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    depositAccountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    depositAccountBasicEntity.setAccountNum(accountSummary.getAccountNum());
    depositAccountBasicEntity.setSeqno(accountSummary.getSeqno());

    // load existing account entity
    DepositAccountBasicEntity existingDepositAccountBasicEntity = depositAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), accountSummary.getAccountNum(),
            accountSummary.getSeqno(), depositAccountBasic.getCurrencyCode());

    // copy PK for update
    if (existingDepositAccountBasicEntity != null) {
      depositAccountBasicEntity.setId(existingDepositAccountBasicEntity.getId());
    }

    // upsert deposit account basic and insert history if needed
    if (!ObjectComparator.isSame(depositAccountBasicEntity, existingDepositAccountBasicEntity, "syncedAt")) {
      depositAccountBasicRepository.save(depositAccountBasicEntity);
      depositAccountBasicHistoryRepository.save(
          depositAccountBasicHistoryMapper.toDepositAccountBasicHistoryEntity(depositAccountBasicEntity));
    }
  }

  private Organization getOrganization(ExecutionContext executionContext) {
    return Organization.builder()
        .organizationCode("020") // TODO jayden-lee implement organizationCode
        .build();
  }
}
