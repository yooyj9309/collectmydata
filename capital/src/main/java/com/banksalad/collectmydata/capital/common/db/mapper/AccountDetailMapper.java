package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountDetailMapper {

  @Mapping(source = "accountDetailResponse.nextRepayDate", target = "nextRepayDate", dateFormat = "yyyyMMdd")
  AccountDetailEntity toAccountDetailEntityFrom(AccountDetailResponse accountDetailResponse);

  AccountDetail toAccountDetailFrom(AccountDetailEntity accountDetailEntity);
}
