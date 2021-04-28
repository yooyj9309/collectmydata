package com.banksalad.collectmydata.mock.irp.service.mapper;

import com.banksalad.collectmydata.mock.common.db.entity.BankIrpAccountSummaryEntity;
import com.banksalad.collectmydata.mock.common.db.entity.InvestIrpAccountSummaryEntity;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IrpAccountSummaryMapper {

  @Mapping(target = "isConsent", constant = "true")
  IrpAccountSummary entityToDto(BankIrpAccountSummaryEntity bankIrpAccountSummaryEntity);

  @Mapping(target = "isConsent", constant = "true")
  IrpAccountSummary entityToDto(InvestIrpAccountSummaryEntity investIrpAccountSummaryEntity);
}
