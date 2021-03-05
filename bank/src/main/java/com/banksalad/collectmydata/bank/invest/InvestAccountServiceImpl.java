package com.banksalad.collectmydata.bank.invest;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.InvestAccountBasicHistoryMapper;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.InvestAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountBasicHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.common.service.ExternalApiService;
import com.banksalad.collectmydata.bank.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicResponse;
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

  private final AccountSummaryService accountSummaryService;
  private final UserSyncStatusService userSyncStatusService;
  private final ExternalApiService externalApiService;
  private final InvestAccountBasicRepository investAccountBasicRepository;
  private final InvestAccountBasicHistoryRepository investAccountBasicHistoryRepository;

  private final InvestAccountBasicMapper investAccountBasicMapper = Mappers
      .getMapper(InvestAccountBasicMapper.class);

  private final InvestAccountBasicHistoryMapper investAccountBasicHistoryMapper = Mappers
      .getMapper(InvestAccountBasicHistoryMapper.class);

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

    if (!ObjectComparator.isSame(investAccountBasicEntity, existingInvestAccountBasicEntity, "syncedAt")) {
      investAccountBasicRepository.save(investAccountBasicEntity);
      investAccountBasicHistoryRepository
          .save(investAccountBasicHistoryMapper.toInvestAccountBasicHistoryEntity(investAccountBasicEntity));
    }
  }

  @Override
  public List<InvestAccountDetail> listInvestAccountDetails(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries) {
    return null;
  }

  private Organization getOrganization(ExecutionContext executionContext) {
    return Organization.builder()
        .organizationCode("020") // TODO jayden-lee implement organizationCode
        .build();
  }
}
