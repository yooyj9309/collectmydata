package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  AccountBasicHistoryEntity toHistoryEntity(AccountBasicEntity accountBasicEntity,
      @MappingTarget AccountBasicHistoryEntity accountBasicHistoryEntity);
}
