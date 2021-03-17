package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.account.dto.AccountBasic;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountBasicMapper {

  AccountBasicEntity toAccountBasicEntityFrom(AccountBasic accountBasic);
}
