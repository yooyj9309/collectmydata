package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.account.dto.AccountBasic;
import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountBasicMapper {

  @Mappings(
      value = {
          @Mapping(source = "accountBasicResponse.issueDate", target = "issueDate", dateFormat = "yyyyMMdd"),
          @Mapping(source = "accountBasicResponse.expDate", target = "expDate", dateFormat = "yyyyMMdd")
      }
  )
  AccountBasicEntity toAccountBasicEntityFrom(AccountBasicResponse accountBasicResponse);

  AccountBasic toAccountBasicFrom(AccountBasicEntity accountBasicEntity);
}
