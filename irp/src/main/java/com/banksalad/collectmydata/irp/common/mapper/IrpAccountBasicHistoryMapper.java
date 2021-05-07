package com.banksalad.collectmydata.irp.common.mapper;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface IrpAccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  IrpAccountBasicHistoryEntity toHistoryEntity(IrpAccountBasicEntity irpAccountBasicEntity);

  @Mapping(target = "id", ignore = true)
  IrpAccountBasicHistoryEntity entityToHistoryEntity(IrpAccountBasicEntity irpAccountBasicEntity,
      @MappingTarget IrpAccountBasicHistoryEntity irpAccountBasicHistoryEntity);
}
