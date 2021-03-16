package com.banksalad.collectmydata.irp.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.irp.collect.Apis;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountSummaryMapper;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummariesResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.ListIrpAccountSummariesRequest;
import com.banksalad.collectmydata.irp.common.service.IrpInformationProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrpAccountSummaryServiceImpl implements IrpAccountSummaryService {

  private final SummaryService<ListIrpAccountSummariesRequest, IrpAccountSummary> irpAccountSummaryServices;

  private final SummaryRequestHelper<ListIrpAccountSummariesRequest> irpAccountSummariesRequestHelper;

  private final SummaryResponseHelper<IrpAccountSummary> irpAccountSummaryResponseHelper;

  private final IrpAccountSummaryRepository irpAccountSummaryRepository;

  private final IrpAccountSummaryMapper irpAccountSummaryMapper = Mappers.getMapper(IrpAccountSummaryMapper.class);

  @Override
  public List<IrpAccountSummary> listAccountSummaries(ExecutionContext executionContext)
      throws ResponseNotOkException {

    irpAccountSummaryServices.listAccountSummaries(executionContext, Executions.irp_get_accounts, irpAccountSummariesRequestHelper, irpAccountSummaryResponseHelper);

    return getRequiringConsentAccountSummaries(executionContext);
  }

  private Organization getOrganization(ExecutionContext executionContext) {

    // TODO: Organization
    return Organization.builder()
        .organizationCode("020")
        .build();
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
  @Transactional
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, IrpAccountSummary accountSummary,
      long basicSearchTimestamp) {

    IrpAccountSummaryEntity irpAccountSummaryEntity = irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElseThrow(() -> new CollectRuntimeException("No Irp Account Summary Data"));

    irpAccountSummaryEntity.setBasicSearchTimestamp(basicSearchTimestamp);

    irpAccountSummaryRepository.save(irpAccountSummaryEntity);
  }

  @Override
  @Transactional
  public void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      long detailSearchTimestamp) {

    IrpAccountSummaryEntity irpAccountSummaryEntity = irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, irpAccountSummary.getAccountNum(), irpAccountSummary.getSeqno())
        .orElseThrow(() -> new CollectRuntimeException("No Irp Account Summary Data"));

    irpAccountSummaryEntity.setDetailSearchTimestamp(detailSearchTimestamp);

    irpAccountSummaryRepository.save(irpAccountSummaryEntity);
  }
}
