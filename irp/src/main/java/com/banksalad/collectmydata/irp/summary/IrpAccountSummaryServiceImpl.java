package com.banksalad.collectmydata.irp.summary;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountSummaryMapper;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.ListIrpAccountSummariesRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;
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
  public void saveAccountSummaries(ExecutionContext executionContext)
      throws ResponseNotOkException {
    irpAccountSummaryServices
        .listAccountSummaries(executionContext, Executions.irp_get_accounts, irpAccountSummariesRequestHelper,
            irpAccountSummaryResponseHelper);
  }

  @Override
  public List<IrpAccountSummary> listConsentedAccountSummaries(long banksaladUserId, String organizationId) {

    List<IrpAccountSummaryEntity> irpAccountListEntities = irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(banksaladUserId, organizationId, true);

    return irpAccountListEntities.stream()
        .map(irpAccountSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  private Organization getOrganization(ExecutionContext executionContext) {

    // TODO: Organization
    return Organization.builder()
        .organizationCode("020")
        .build();
  }

  @Override
  @Transactional
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, IrpAccountSummary accountSummary,
      long basicSearchTimestamp) {

    getIrpAccountSummaryEntity(banksaladUserId, organizationId, accountSummary)
        .setBasicSearchTimestamp(basicSearchTimestamp);

    irpAccountSummaryRepository.save(getIrpAccountSummaryEntity(banksaladUserId, organizationId,
        accountSummary));
  }

  @Override
  @Transactional
  public void updateDetailSearchTimestamp(long banksaladUserId, String organizationId,
      IrpAccountSummary irpAccountSummary,
      long detailSearchTimestamp) {

    IrpAccountSummaryEntity irpAccountSummaryEntity = getIrpAccountSummaryEntity(banksaladUserId, organizationId,
        irpAccountSummary);

    irpAccountSummaryEntity.setDetailSearchTimestamp(detailSearchTimestamp);

    irpAccountSummaryRepository.save(irpAccountSummaryEntity);
  }

  @Override
  public void updateBasicResponseCode(long banksaladUserId, String organizationId, IrpAccountSummary accountSummary,
      String responseCode) {

    IrpAccountSummaryEntity irpAccountSummaryEntity = getIrpAccountSummaryEntity(banksaladUserId, organizationId,
        accountSummary);

    irpAccountSummaryEntity.setBasicResponseCode(responseCode);

    irpAccountSummaryRepository.save(irpAccountSummaryEntity);
  }

  @Override
  public void updateDetailResponseCode(long banksaladUserId, String organizationId, IrpAccountSummary accountSummary,
      String responseCode) {

  }

  private IrpAccountSummaryEntity getIrpAccountSummaryEntity(long banksaladUserId, String organizationId,
      IrpAccountSummary accountSummary) {

    return irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElseThrow(() -> new CollectRuntimeException("No Irp Account Summary Data"));
  }
}
