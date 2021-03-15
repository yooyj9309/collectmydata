package com.banksalad.collectmydata.irp.common.db.entity.mapper;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IrpAccountDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  IrpAccountDetailHistoryEntity toHistoryEntity(IrpAccountDetailEntity irpAccountDetailEntity);
}
