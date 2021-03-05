package com.banksalad.collectmydata.capital.common.db.entity.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.capital.loan.dto.AccountBasic;
import com.banksalad.collectmydata.capital.loan.dto.AccountBasicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountBasicMapper {

  @Mappings(
      value = {
          @Mapping(source = "accountBasicResponse.expDate", target = "expDate", dateFormat = "yyyyMMdd"),
          @Mapping(source = "accountBasicResponse.issueDate", target = "issueDate", dateFormat = "yyyyMMdd")
      }
  )
  void merge(AccountBasicResponse accountBasicResponse, @MappingTarget AccountBasicEntity accountBasicEntity);

  AccountBasic toAccountBasicFrom(AccountBasicEntity accountBasicEntity);
}
