package com.banksalad.collectmydata.telecom.common.mapper;

import com.banksalad.collectmydata.telecom.common.db.entity.TelecomBillEntity;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomBillHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TelecomBillHistoryMapper {
  
  @Mapping(target = "id", ignore = true)
  TelecomBillHistoryEntity toHistoryEntity(TelecomBillEntity telecomBillEntity);
}
