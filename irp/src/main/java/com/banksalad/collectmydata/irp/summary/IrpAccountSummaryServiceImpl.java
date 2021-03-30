package com.banksalad.collectmydata.irp.summary;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.ListIrpAccountSummariesRequest;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountSummaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
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

  @Override
  @Transactional
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId,
      IrpAccountSummary irpAccountSummary,
      long basicSearchTimestamp) {

    updateIrpAccountSummaryEntity(banksaladUserId, organizationId, irpAccountSummary,
        irpAccountSummaryEntity -> irpAccountSummaryEntity.setBasicSearchTimestamp(basicSearchTimestamp));
  }

  @Override
  @Transactional
  public void updateDetailSearchTimestamp(long banksaladUserId, String organizationId,
      IrpAccountSummary irpAccountSummary,
      long detailSearchTimestamp) {

    updateIrpAccountSummaryEntity(banksaladUserId, organizationId, irpAccountSummary,
        irpAccountSummaryEntity -> irpAccountSummaryEntity.setDetailSearchTimestamp(detailSearchTimestamp));
  }

  @Override
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId,
      IrpAccountSummary irpAccountSummary, LocalDateTime transactionSyncedAt) {

    updateIrpAccountSummaryEntity(banksaladUserId, organizationId, irpAccountSummary,
        irpAccountSummaryEntity -> irpAccountSummaryEntity.setTransactionSyncedAt(transactionSyncedAt));
  }

  @Override
  public void updateBasicResponseCode(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      String responseCode) {

    updateIrpAccountSummaryEntity(banksaladUserId, organizationId, irpAccountSummary,
        irpAccountSummaryEntity -> irpAccountSummaryEntity.setBasicResponseCode(responseCode));
  }

  @Override
  public void updateDetailResponseCode(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      String responseCode) {

    updateIrpAccountSummaryEntity(banksaladUserId, organizationId, irpAccountSummary,
        irpAccountSummaryEntity -> irpAccountSummaryEntity.setDetailResponseCode(responseCode));
  }

  @Override
  public void updateTransactionResponseCode(long banksaladUserId, String organizationId,
      IrpAccountSummary irpAccountSummary, String responseCode) {

    updateIrpAccountSummaryEntity(banksaladUserId, organizationId, irpAccountSummary,
        irpAccountSummaryEntity -> irpAccountSummaryEntity.setTransactionResponseCode(responseCode));
  }

  private void updateIrpAccountSummaryEntity(long banksaladUserId, String organizationId,
      IrpAccountSummary accountSummary, Consumer<IrpAccountSummaryEntity> consumer) {

    irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(irpAccountSummaryEntity -> {

          consumer.accept(irpAccountSummaryEntity);
          irpAccountSummaryRepository.save(irpAccountSummaryEntity);
        });
  }
}
