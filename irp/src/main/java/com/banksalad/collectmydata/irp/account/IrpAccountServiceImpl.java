package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.irp.collect.Apis;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountDetailHistoryMapper;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountDetailMapper;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasic;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailsResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.service.IrpInformationProviderService;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
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

  private final AccountInfoService<IrpAccountSummary, IrpAccountBasicRequest, IrpAccountBasic> accountBasicAccountInfoService;

  private final AccountInfoRequestHelper<IrpAccountBasicRequest, IrpAccountSummary> accountInfoRequestHelper;

  private final AccountInfoResponseHelper<IrpAccountSummary, IrpAccountBasic> accountInfoResponseHelper;

  private final IrpInformationProviderService irpInformationProviderService;

  private final IrpAccountSummaryService irpAccountSummaryService;

  private final IrpAccountDetailRepository irpAccountDetailRepository;

  private final IrpAccountDetailHistoryRepository irpAccountDetailHistoryRepository;

  private final UserSyncStatusService userSyncStatusService;

  private final IrpAccountDetailMapper irpAccountDetailMapper = Mappers
      .getMapper(IrpAccountDetailMapper.class);

  private final IrpAccountDetailHistoryMapper irpAccountDetailHistoryMapper = Mappers
      .getMapper(IrpAccountDetailHistoryMapper.class);

  @Override
  public List<IrpAccountBasic> getIrpAccountBasics(ExecutionContext executionContext) {
    return accountBasicAccountInfoService
        .listAccountInfos(executionContext, Executions.irp_get_basic, accountInfoRequestHelper,
            accountInfoResponseHelper);
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

      irpAccountDetailRepository
          .deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(executionContext.getBanksaladUserId(),
              executionContext.getOrganizationId(), irpAccountSummary.getAccountNum(),
              irpAccountSummary.getSeqno());
    }

    short irpDetailNo = 0;
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
