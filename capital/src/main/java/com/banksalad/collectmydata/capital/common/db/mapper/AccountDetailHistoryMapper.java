package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AccountDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  AccountDetailHistoryEntity toAccountDetailHistoryEntityFrom(AccountDetailEntity accountDetailEntity);
}
