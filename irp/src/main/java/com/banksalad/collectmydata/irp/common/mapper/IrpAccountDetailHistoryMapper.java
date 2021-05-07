package com.banksalad.collectmydata.irp.common.mapper;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface IrpAccountDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  IrpAccountDetailHistoryEntity toHistoryEntity(IrpAccountDetailEntity irpAccountDetailEntity);

  @Mapping(target = "id", ignore = true)
  IrpAccountDetailHistoryEntity entityToHistoryEntity(IrpAccountDetailEntity irpAccountDetailEntity,
      @MappingTarget IrpAccountDetailHistoryEntity irpAccountDetailHistoryEntity);
}
