package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.ConsentEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ConsentHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ConsentHistoryMapper {

  @Mapping(target = "id", ignore = true)
  ConsentHistoryEntity toHistoryEntity(ConsentEntity consentEntity);
}
