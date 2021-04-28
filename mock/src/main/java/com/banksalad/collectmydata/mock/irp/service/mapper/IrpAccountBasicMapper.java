package com.banksalad.collectmydata.mock.irp.service.mapper;

import com.banksalad.collectmydata.mock.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasic;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IrpAccountBasicMapper {

  IrpAccountBasic entityToDto(IrpAccountBasicEntity irpAccountBasicEntity);
}
