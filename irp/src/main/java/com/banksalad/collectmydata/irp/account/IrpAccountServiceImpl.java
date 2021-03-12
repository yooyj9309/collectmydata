package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.irp.collect.Apis;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountBasicHistoryMapper;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountBasicMapper;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
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
public class IrpAccountServiceImpl implements IrpAccountService {

  private final IrpInformationProviderService irpInformationProviderService;

  private final IrpAccountSummaryService irpAccountSummaryService;

  private final IrpAccountBasicRepository irpAccountBasicRepository;

  private final IrpAccountBasicHistoryRepository irpAccountBasicHistoryRepository;

  private final UserSyncStatusService userSyncStatusService;

  private final IrpAccountBasicMapper irpAccountBasicMapper = Mappers
      .getMapper(IrpAccountBasicMapper.class);

  private final IrpAccountBasicHistoryMapper irpAccountBasicHistoryMapper = Mappers
      .getMapper(IrpAccountBasicHistoryMapper.class);

  @Override
  public List<IrpAccountBasicResponse> getIrpAccountBasics(ExecutionContext executionContext,
      List<IrpAccountSummary> accountSummaries) {

    Organization organization = getOrganization(executionContext);

    for (IrpAccountSummary irpAccountSummary : accountSummaries) {

      IrpAccountBasicResponse irpAccountBasicResponse = irpInformationProviderService.getAccountBasic(
          executionContext, organization, irpAccountSummary);

      try {

        saveIrpAccountBasic(executionContext, irpAccountSummary, irpAccountBasicResponse);

        irpAccountSummaryService.updateBasicSearchTimestamp(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), irpAccountSummary, irpAccountBasicResponse.getSearchTimestamp());

        // Api 200 Ok
        userSyncStatusService
            .updateUserSyncStatus(
                executionContext.getBanksaladUserId(),
                executionContext.getOrganizationId(),
                Apis.irp_get_accounts.getId(),
                executionContext.getSyncStartedAt(),
                true
            );
      } catch (Exception e) {
        log.error("Failed to save irp account basic", e);
      }
    }

    List<IrpAccountBasicEntity> irpAccountBasicEntities = irpAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId());

    return irpAccountBasicEntities.stream()
        .map(irpAccountBasicMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<IrpAccountDetail> listIrpAccountDetails(ExecutionContext executionContext,
      List<IrpAccountSummary> accountSummaries) {
    return null;
  }

  private Organization getOrganization(ExecutionContext executionContext) {
    return Organization.builder()
        .organizationCode("020") // TODO: implement organizationCode
        .build();
  }

  private void saveIrpAccountBasic(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      IrpAccountBasicResponse irpAccountBasicResponse) {

    // convert to entity
    IrpAccountBasicEntity irpAccountBasicEntity = irpAccountBasicMapper.dtoToEntity(irpAccountBasicResponse);
    irpAccountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    irpAccountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    irpAccountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    irpAccountBasicEntity.setAccountNum(irpAccountSummary.getAccountNum());
    irpAccountBasicEntity.setSeqno(irpAccountSummary.getSeqno());

    // load existing account entity
    IrpAccountBasicEntity existingIrpAccountBasicEntity = irpAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), irpAccountSummary.getAccountNum(),
            irpAccountSummary.getSeqno()).orElseGet(() -> IrpAccountBasicEntity.builder().build());

    // copy PK for update
    if (existingIrpAccountBasicEntity != null) {
      irpAccountBasicEntity.setId(existingIrpAccountBasicEntity.getId());
    }

    // upsert irp account basic and insert history if needed
    if (!ObjectComparator.isSame(irpAccountBasicEntity, existingIrpAccountBasicEntity, "syncedAt")) {

      irpAccountBasicRepository.save(irpAccountBasicEntity);
      irpAccountBasicHistoryRepository.save(
          irpAccountBasicHistoryMapper.toHistoryEntity(irpAccountBasicEntity));
    }
  }
}
