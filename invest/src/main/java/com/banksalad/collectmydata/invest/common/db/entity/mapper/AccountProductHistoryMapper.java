package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.invest.common.db.entity.AccountProductEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountProductHistoryMapper {

  @Mapping(target = "id", ignore = true)
  AccountProductHistoryEntity toHistoryEntity(AccountProductEntity accountProductEntity,
      @MappingTarget AccountProductHistoryEntity accountProductHistoryEntity);
}
