package com.banksalad.collectmydata.irp.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummariesResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountSummaryMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class IrpSummaryResponseHelper implements SummaryResponseHelper<IrpAccountSummary> {

  private final IrpAccountSummaryRepository irpAccountSummaryRepository;
  private final IrpAccountSummaryMapper irpAccountSummaryMapper = Mappers.getMapper(IrpAccountSummaryMapper.class);

  @Override
  public Iterator<IrpAccountSummary> iterator(SummaryResponse response) {
    return ((IrpAccountSummariesResponse) response).getIrpAccountSummaries().iterator();
  }

  @Override
  public void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response) {

  }

  @Override
  public void saveSummary(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary) {

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
