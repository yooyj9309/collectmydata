package com.banksalad.collectmydata.bank.deposit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.DepositAccountBasicHistoryMapper;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.DepositAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.DepositAccountDetailHistoryMapper;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.DepositAccountDetailMapper;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountBasicHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailRepository;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.common.service.ExternalApiService;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class DepositAccountServiceImpl implements DepositAccountService {

  private final AccountSummaryService accountSummaryService;
  private final ExternalApiService externalApiService;

  private final DepositAccountBasicRepository depositAccountBasicRepository;
  private final DepositAccountBasicHistoryRepository depositAccountBasicHistoryRepository;
  private final DepositAccountDetailRepository depositAccountDetailRepository;
  private final DepositAccountDetailHistoryRepository depositAccountDetailHistoryRepository;

  private final DepositAccountBasicMapper depositAccountBasicMapper = Mappers
      .getMapper(DepositAccountBasicMapper.class);

  private final DepositAccountBasicHistoryMapper depositAccountBasicHistoryMapper = Mappers
      .getMapper(DepositAccountBasicHistoryMapper.class);

  private final DepositAccountDetailMapper depositAccountDetailMapper = Mappers
      .getMapper(DepositAccountDetailMapper.class);

  private final DepositAccountDetailHistoryMapper depositAccountDetailHistoryMapper = Mappers
      .getMapper(DepositAccountDetailHistoryMapper.class);

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
        saveDepositAccountBasic(executionContext, accountSummary, depositAccountBasic);

        accountSummaryService.updateBasicSearchTimestamp(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary, depositAccountBasic.getSearchTimestamp());
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
          depositAccountBasicHistoryMapper.toHistoryEntity(depositAccountBasicEntity));
    }
  }

  @Override
  public List<DepositAccountDetail> listDepositAccountDetails(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries) {

    Organization organization = getOrganization(executionContext);

    for (AccountSummary accountSummary : accountSummaries) {
      GetDepositAccountDetailResponse depositAccountDetailResponse = externalApiService.getDepositAccountDetail(
          executionContext, organization.getOrganizationCode(), accountSummary.getAccountNum(),
          accountSummary.getSeqno(), accountSummary.getBasicSearchTimestamp());

      try {
        saveDepositAccountDetail(executionContext, accountSummary,
            depositAccountDetailResponse.getDepositAccountDetails());

        accountSummaryService.updateDetailSearchTimestamp(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary, depositAccountDetailResponse.getSearchTimestamp());
      } catch (Exception e) {
        log.error("Failed to save deposit account detail", e);
      }
    }

    // Api 200 Ok, userSyncStatusService.upsert(executionContext);

    List<DepositAccountDetailEntity> depositAccountDetailEntities = depositAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId());

    return depositAccountDetailEntities.stream()
        .map(depositAccountDetailMapper::entityToDto)
        .collect(Collectors.toList());
  }

  private void saveDepositAccountDetail(ExecutionContext executionContext, AccountSummary accountSummary,
      List<DepositAccountDetail> depositAccountDetails) {

    for (DepositAccountDetail depositAccountDetail : depositAccountDetails) {

      // convert to entity
      DepositAccountDetailEntity depositAccountDetailEntity = depositAccountDetailMapper
          .dtoToEntity(depositAccountDetail);
      depositAccountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      depositAccountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
      depositAccountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
      depositAccountDetailEntity.setAccountNum(accountSummary.getAccountNum());
      depositAccountDetailEntity.setSeqno(accountSummary.getSeqno());

      // load existing account detail entity
      DepositAccountDetailEntity existingDepositAccountDetailEntity = depositAccountDetailRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
              accountSummary.getAccountNum(),
              accountSummary.getSeqno(), depositAccountDetail.getCurrencyCode());

      // copy PK for update
      if (existingDepositAccountDetailEntity != null) {
        depositAccountDetailEntity.setId(existingDepositAccountDetailEntity.getId());
      }

      // upsert deposit account detail and insert history if needed
      if (!ObjectComparator.isSame(depositAccountDetailEntity, existingDepositAccountDetailEntity, "syncedAt")) {
        depositAccountDetailRepository.save(depositAccountDetailEntity);
        depositAccountDetailHistoryRepository
            .save(depositAccountDetailHistoryMapper.toHistoryEntity(depositAccountDetailEntity));
      }
    }
  }

  private Organization getOrganization(ExecutionContext executionContext) {
    return Organization.builder()
        .organizationCode("020") // TODO jayden-lee implement organizationCode
        .build();
  }
}
