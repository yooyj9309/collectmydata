package com.banksalad.collectmydata.mock.irp.service.mapper;

import com.banksalad.collectmydata.mock.common.db.entity.BankIrpAccountDetailEntity;
import com.banksalad.collectmydata.mock.common.db.entity.InvestIrpAccountDetailEntity;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountDetail;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IrpAccountDetailMapper {

  IrpAccountDetail entityToDto(BankIrpAccountDetailEntity bankIrpAccountDetailEntity);

  IrpAccountDetail entityToDto(InvestIrpAccountDetailEntity investIrpAccountDetailEntity);
}
