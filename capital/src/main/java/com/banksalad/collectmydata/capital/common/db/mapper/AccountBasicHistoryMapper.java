package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  AccountBasicHistoryEntity toHistoryEntity(AccountBasicEntity accountBasicEntity);
}
