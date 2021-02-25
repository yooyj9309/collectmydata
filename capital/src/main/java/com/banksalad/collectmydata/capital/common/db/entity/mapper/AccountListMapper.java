package com.banksalad.collectmydata.capital.common.db.entity.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.capital.common.dto.Account;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface AccountListMapper {

  @Mappings(
      value = {
          @Mapping(target = "basicSearchTimestamp", ignore = true),
          @Mapping(target = "detailSearchTimestamp", ignore = true),
          @Mapping(target = "operatingLeaseBasicSearchTimestamp", ignore = true)
      }
  )
  void merge(ExecutionContext context, Account account, @MappingTarget AccountListEntity entity);
  
  Account entityToDto(AccountListEntity entity);
}
