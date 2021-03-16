package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.irp.collect.Apis;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountBasicHistoryMapper;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountBasicMapper;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountDetailHistoryMapper;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountDetailMapper;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailsResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.service.IrpInformationProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Comparator;
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

  private final IrpAccountDetailRepository irpAccountDetailRepository;

  private final IrpAccountDetailHistoryRepository irpAccountDetailHistoryRepository;

  private final UserSyncStatusService userSyncStatusService;

  private final IrpAccountBasicMapper irpAccountBasicMapper = Mappers
      .getMapper(IrpAccountBasicMapper.class);

  private final IrpAccountBasicHistoryMapper irpAccountBasicHistoryMapper = Mappers
      .getMapper(IrpAccountBasicHistoryMapper.class);

  private final IrpAccountDetailMapper irpAccountDetailMapper = Mappers
      .getMapper(IrpAccountDetailMapper.class);

  private final IrpAccountDetailHistoryMapper irpAccountDetailHistoryMapper = Mappers
      .getMapper(IrpAccountDetailHistoryMapper.class);

  @Override
  public List<IrpAccountBasicResponse> getIrpAccountBasics(ExecutionContext executionContext,
      List<IrpAccountSummary> irpAccountSummaries) {

    Organization organization = getOrganization(executionContext);

    for (IrpAccountSummary irpAccountSummary : irpAccountSummaries) {

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
                Apis.irp_get_basic.getId(),
                executionContext.getSyncStartedAt(),
                irpAccountBasicResponse.getSearchTimestamp()
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
      List<IrpAccountSummary> irpAccountSummaries) {

    Organization organization = getOrganization(executionContext);

    for (IrpAccountSummary irpAccountSummary : irpAccountSummaries) {

      IrpAccountDetailsResponse irpAccountDetailsResponse = irpInformationProviderService.getAccountDetails(
          executionContext, organization, irpAccountSummary);

      try {

        deleteAndSaveIrpAccountDetail(executionContext, irpAccountSummary,
            irpAccountDetailsResponse.getIrpAccountDetails());

        irpAccountSummaryService.updateDetailSearchTimestamp(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), irpAccountSummary, irpAccountDetailsResponse.getSearchTimestamp());

        // Api 200 Ok
        userSyncStatusService
            .updateUserSyncStatus(
                executionContext.getBanksaladUserId(),
                executionContext.getOrganizationId(),
                Apis.irp_get_detail.getId(),
                executionContext.getSyncStartedAt(),
                irpAccountDetailsResponse.getSearchTimestamp()
            );
      } catch (Exception e) {
        log.error("Failed to save irp account detail", e);
      }
    }

    List<IrpAccountDetailEntity> irpAccountDetailEntities = irpAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId());

    return irpAccountDetailEntities.stream()
        .map(irpAccountDetailMapper::entityToDto)
        .collect(Collectors.toList());
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

  private void deleteAndSaveIrpAccountDetail(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      List<IrpAccountDetail> apiIrpAccountDetails) {

    List<IrpAccountDetail> originalApiIrpAccountDetails = new ArrayList<>(apiIrpAccountDetails);
    apiIrpAccountDetails.sort(getComparator());

    Javers javers = JaversBuilder.javers()
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();

    List<IrpAccountDetail> dbIrpAccountDetails = irpAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), irpAccountSummary.getAccountNum(),
            irpAccountSummary.getSeqno())
        .stream()
        .map(irpAccountDetailMapper::entityToDto)
        .sorted(getComparator())
        .collect(Collectors.toList());

    Diff diff = javers.compareCollections(dbIrpAccountDetails, apiIrpAccountDetails, IrpAccountDetail.class);

    if (diff.getChanges().size() > 0) {

      irpAccountDetailRepository.deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), irpAccountSummary.getAccountNum(),
        irpAccountSummary.getSeqno());
    }

    int irpDetailNo = 0;
    for (IrpAccountDetail irpAccountDetail : originalApiIrpAccountDetails) {

      // convert to entity
      IrpAccountDetailEntity irpAccountDetailEntity = irpAccountDetailMapper
          .dtoToEntity(irpAccountDetail);
      irpAccountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      irpAccountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
      irpAccountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
      irpAccountDetailEntity.setAccountNum(irpAccountSummary.getAccountNum());
      irpAccountDetailEntity.setSeqno(irpAccountSummary.getSeqno());
      irpAccountDetailEntity.setIrpDetailNo(irpDetailNo++);

      if (diff.getChanges().size() > 0) {
        irpAccountDetailRepository.save(irpAccountDetailEntity);
      }

      irpAccountDetailHistoryRepository
          .save(irpAccountDetailHistoryMapper.toHistoryEntity(irpAccountDetailEntity));
    }
  }

  private Comparator<IrpAccountDetail> getComparator() {

    return Comparator.comparing(IrpAccountDetail::getIrpName)
        .thenComparing(IrpAccountDetail::getOpenDate)
        .thenComparing(IrpAccountDetail::getIrpType)
        .thenComparing(IrpAccountDetail::getEvalAmt)
        .thenComparing(IrpAccountDetail::getInvPrincipal);
  }
}
