package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.irp.collect.Apis;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountSummaryMapper;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummariesResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.service.IrpInformationProviderService;
import com.banksalad.collectmydata.irp.common.service.UserSyncStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrpAccountSummaryServiceImpl implements IrpAccountSummaryService {

  private final UserSyncStatusService userSyncStatusService;
  private final IrpInformationProviderService irpInformationProviderService;
  private final IrpAccountSummaryRepository irpAccountSummaryRepository;
  private final IrpAccountSummaryMapper irpAccountSummaryMapper = Mappers.getMapper(IrpAccountSummaryMapper.class);

  @Override
  @Transactional
  public List<IrpAccountSummary> listAccountSummaries(ExecutionContext executionContext) {

    Organization organization = getOrganization(executionContext);

    long searchTimestamp = userSyncStatusService
        .getSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            Apis.irp_get_accounts.getId());

    IrpAccountSummariesResponse irpAccountSummariesResponse = irpInformationProviderService
        .getIrpAccountSummaries(executionContext, organization.getOrganizationCode(), searchTimestamp);

    saveAccountSummaries(executionContext, irpAccountSummariesResponse);

    // Api 200 Ok
    userSyncStatusService
        .updateUserSyncStatus(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            Apis.irp_get_accounts.getId(),
            executionContext.getSyncStartedAt(),
            irpAccountSummariesResponse.getSearchTimestamp(),
            true
        );

    return getRequiringConsentAccountSummaries(executionContext);
  }

  private Organization getOrganization(ExecutionContext executionContext) {

    // TODO: Organization
    return Organization.builder()
        .organizationCode("020")
        .build();
  }

  private void saveAccountSummaries(ExecutionContext executionContext,
      IrpAccountSummariesResponse irpAccountSummariesResponse) {

    List<IrpAccountSummary> irpAccountSummaries = irpAccountSummariesResponse.getIrpAccountSummaries();

    for (IrpAccountSummary irpAccountSummary : irpAccountSummaries) {

      IrpAccountSummaryEntity irpAccountSummaryEntity = irpAccountSummaryRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
              irpAccountSummary.getAccountNum(),
              irpAccountSummary.getSeqno()).orElseGet(() -> IrpAccountSummaryEntity.builder().build());

      irpAccountSummaryMapper.merge(irpAccountSummary, irpAccountSummaryEntity);

      irpAccountSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      irpAccountSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
      irpAccountSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
      irpAccountSummaryRepository.save(irpAccountSummaryEntity);
    }
  }

  private List<IrpAccountSummary> getRequiringConsentAccountSummaries(ExecutionContext executionContext) {

    List<IrpAccountSummaryEntity> irpAccountListEntities = irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), true);

    return irpAccountListEntities.stream()
        .map(irpAccountSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, IrpAccountSummary accountSummary,
      long basicSearchTimestamp) {
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      long detailSearchTimestamp) {
  }
}
