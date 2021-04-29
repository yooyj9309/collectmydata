package com.banksalad.collectmydata.mock.irp.service.mapper;

import com.banksalad.collectmydata.mock.common.db.entity.BankIrpAccountTransactionEntity;
import com.banksalad.collectmydata.mock.common.db.entity.InvestIrpAccountTransactionEntity;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IrpAccountTransactionMapper {

  IrpAccountTransaction entityToDto(BankIrpAccountTransactionEntity bankIrpAccountTransactionEntity);

  IrpAccountTransaction entityToDto(InvestIrpAccountTransactionEntity investIrpAccountTransactionEntity);
}
