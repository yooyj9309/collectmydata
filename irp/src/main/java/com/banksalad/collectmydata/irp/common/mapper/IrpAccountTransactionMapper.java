package com.banksalad.collectmydata.irp.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountTransactionEntity;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IrpAccountTransactionMapper {

  IrpAccountTransactionEntity dtoToEntity(IrpAccountTransaction irpAccountTransaction);
}
