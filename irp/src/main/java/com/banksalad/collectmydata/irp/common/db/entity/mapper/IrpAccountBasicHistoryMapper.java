package com.banksalad.collectmydata.irp.common.db.entity.mapper;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IrpAccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  IrpAccountBasicHistoryEntity toHistoryEntity(IrpAccountBasicEntity irpAccountBasicEntity);
}
