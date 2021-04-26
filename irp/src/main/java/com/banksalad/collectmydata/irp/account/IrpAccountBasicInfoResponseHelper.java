package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasic;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountBasicHistoryMapper;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountBasicMapper;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class IrpAccountBasicInfoResponseHelper implements
    AccountInfoResponseHelper<IrpAccountSummary, IrpAccountBasic> {

  private final IrpAccountBasicRepository irpAccountBasicRepository;

  private final IrpAccountBasicHistoryRepository irpAccountBasicHistoryRepository;

  private final IrpAccountBasicMapper irpAccountBasicMapper = Mappers
      .getMapper(IrpAccountBasicMapper.class);

  private final IrpAccountBasicHistoryMapper irpAccountBasicHistoryMapper = Mappers
      .getMapper(IrpAccountBasicHistoryMapper.class);

  private final IrpAccountSummaryService irpAccountSummaryService;

  @Override
  public IrpAccountBasic getAccountFromResponse(AccountResponse accountResponse) {
    return ((IrpAccountBasicResponse) accountResponse).getIrpAccountBasic();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      IrpAccountBasic irpAccountBasic) {

    // convert to entity
    IrpAccountBasicEntity irpAccountBasicEntity = irpAccountBasicMapper.dtoToEntity(irpAccountBasic);
    irpAccountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    irpAccountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    irpAccountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    irpAccountBasicEntity.setAccountNum(irpAccountSummary.getAccountNum());
    irpAccountBasicEntity.setSeqno(irpAccountSummary.getSeqno());

    // TODO : on-demand, scheduler
    irpAccountBasicEntity.setCreatedBy(Optional.ofNullable(irpAccountBasicEntity.getCreatedBy())
        .orElseGet(() -> String.valueOf(executionContext.getBanksaladUserId())));
    irpAccountBasicEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));
    irpAccountBasicEntity.setConsentId(executionContext.getConsentId());
    irpAccountBasicEntity.setSyncRequestId(executionContext.getSyncRequestId());

    // load existing account entity
    IrpAccountBasicEntity existingIrpAccountBasicEntity = irpAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            irpAccountSummary.getAccountNum(),
            irpAccountSummary.getSeqno()).orElse(null);

    // copy PK for update
    if (existingIrpAccountBasicEntity != null) {
      irpAccountBasicEntity.setId(existingIrpAccountBasicEntity.getId());
    }

    // upsert irp account basic and insert history if needed
    if (!ObjectComparator
        .isSame(irpAccountBasicEntity, existingIrpAccountBasicEntity, ENTITY_EXCLUDE_FIELD)) {

      irpAccountBasicRepository.save(irpAccountBasicEntity);
      irpAccountBasicHistoryRepository.save(
          irpAccountBasicHistoryMapper.toHistoryEntity(irpAccountBasicEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      long searchTimestamp) {
    irpAccountSummaryService
        .updateBasicSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            irpAccountSummary, searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      String responseCode) {
    irpAccountSummaryService
        .updateBasicResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            irpAccountSummary, responseCode);
  }
}
