package com.banksalad.collectmydata.bank.invest;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.InvestAccountBasicHistoryMapper;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.InvestAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.InvestAccountDetailHistoryMapper;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.InvestAccountDetailMapper;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountBasicHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountDetailHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.common.service.ExternalApiService;
import com.banksalad.collectmydata.bank.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
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
public class InvestAccountServiceImpl implements InvestAccountService {

  private static final String[] EXCLUDE_FIELDS = {"syncedAt", "createdAt", "updatedAt", "createdBy", "updatedBy"};
  private final AccountSummaryService accountSummaryService;
  private final UserSyncStatusService userSyncStatusService;
  private final ExternalApiService externalApiService;
  private final InvestAccountBasicRepository investAccountBasicRepository;
  private final InvestAccountBasicHistoryRepository investAccountBasicHistoryRepository;
  private final InvestAccountDetailRepository investAccountDetailRepository;
  private final InvestAccountDetailHistoryRepository investAccountDetailHistoryRepository;

  private final InvestAccountBasicMapper investAccountBasicMapper = Mappers
      .getMapper(InvestAccountBasicMapper.class);

  private final InvestAccountBasicHistoryMapper investAccountBasicHistoryMapper = Mappers
      .getMapper(InvestAccountBasicHistoryMapper.class);

  private final InvestAccountDetailMapper investAccountDetailMapper = Mappers
      .getMapper(InvestAccountDetailMapper.class);

  private final InvestAccountDetailHistoryMapper investAccountDetailHistoryMapper = Mappers
      .getMapper(InvestAccountDetailHistoryMapper.class);

  @Override
  public List<InvestAccountBasic> listInvestAccountBasics(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries) {
    Organization organization = getOrganization(executionContext);
    for (AccountSummary accountSummary : accountSummaries) {
      GetInvestAccountBasicResponse investAccountBasicResponse = externalApiService
          .getInvestAccountBasic(executionContext, accountSummary, organization,
              accountSummary.getBasicSearchTimestamp());

      InvestAccountBasic investAccountBasic = investAccountBasicResponse.getInvestAccountBasic();

      try {
        saveInvestAccountBasic(executionContext, accountSummary, investAccountBasic);

        accountSummaryService.updateBasicSearchTimestamp(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary, investAccountBasic.getSearchTimestamp());

      } catch (Exception e) {
        log.error("Failed to save invest account basic", e);
      }
    }

    //userSyncStatusService 로직 추가

    List<InvestAccountBasicEntity> investAccountBasicEntities = investAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId());

    return investAccountBasicEntities.stream()
        .map(investAccountBasicMapper::entityToDto)
        .collect(Collectors.toList());
  }

  private void saveInvestAccountBasic(ExecutionContext executionContext, AccountSummary accountSummary,
      InvestAccountBasic investAccountBasic) {

    InvestAccountBasicEntity investAccountBasicEntity = investAccountBasicMapper.dtoToEntity(investAccountBasic);
    investAccountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    investAccountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    investAccountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    investAccountBasicEntity.setAccountNum(accountSummary.getAccountNum());
    investAccountBasicEntity.setSeqno(accountSummary.getSeqno());

    InvestAccountBasicEntity existingInvestAccountBasicEntity = investAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno()
        );

    if (existingInvestAccountBasicEntity != null) {
      investAccountBasicEntity.setId(existingInvestAccountBasicEntity.getId());
    }

    if (!ObjectComparator.isSame(investAccountBasicEntity, existingInvestAccountBasicEntity, EXCLUDE_FIELDS)) {
      investAccountBasicRepository.save(investAccountBasicEntity);
      investAccountBasicHistoryRepository
          .save(investAccountBasicHistoryMapper.toInvestAccountBasicHistoryEntity(investAccountBasicEntity));
    }
  }

  @Override
  public List<InvestAccountDetail> listInvestAccountDetails(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries) {
    Organization organization = getOrganization(executionContext);
    for (AccountSummary accountSummary : accountSummaries) {
      GetInvestAccountDetailResponse investAccountDetailResponse = externalApiService.getInvestAccountDetail(
          executionContext,
          accountSummary,
          organization,
          accountSummary.getDetailSearchTimestamp());

      InvestAccountDetail investAccountDetail = investAccountDetailResponse.getInvestAccountDetail();

      try {
        saveInvestAccountDetail(executionContext, accountSummary, investAccountDetail);
        accountSummaryService.updateDetailSearchTimestamp(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary, investAccountDetail.getSearchTimestamp());
      } catch (Exception e) {
        log.error("Failed to save invest account detail", e);
      }
    }

    List<InvestAccountDetailEntity> investAccountDetailEntities = investAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId());

    return investAccountDetailEntities.stream()
        .map(investAccountDetailMapper::entityToDto)
        .collect(Collectors.toList());
  }

  private void saveInvestAccountDetail(ExecutionContext executionContext, AccountSummary accountSummary,
      InvestAccountDetail investAccountDetail) {
    InvestAccountDetailEntity investAccountDetailEntity = investAccountDetailMapper.dtoToEntity(investAccountDetail);
    investAccountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    investAccountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
    investAccountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
    investAccountDetailEntity.setAccountNum(accountSummary.getAccountNum());
    investAccountDetailEntity.setAccountNum(accountSummary.getSeqno());

    InvestAccountDetailEntity existingInvestAccountDetailEntity = investAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno(),
            investAccountDetail.getCurrencyCode()
        );

    if (existingInvestAccountDetailEntity != null) {
      investAccountDetailEntity.setId(existingInvestAccountDetailEntity.getId());
    }

    if (!ObjectComparator.isSame(investAccountDetailEntity, existingInvestAccountDetailEntity, EXCLUDE_FIELDS)) {
      investAccountDetailRepository.save(investAccountDetailEntity);
      investAccountDetailHistoryRepository
          .save(investAccountDetailHistoryMapper.toInvestAccountDetailHistoryEntity(investAccountDetailEntity));
    }
  }

  private Organization getOrganization(ExecutionContext executionContext) {
    return Organization.builder()
        .organizationCode("020") // TODO jayden-lee implement organizationCode
        .build();
  }
}
