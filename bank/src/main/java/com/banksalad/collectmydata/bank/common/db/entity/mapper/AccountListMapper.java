package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.bank.common.dto.Account;
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
          @Mapping(target = "transactionFromDate", ignore = true),
          @Mapping(target = "createdAt", ignore = true),
          @Mapping(target = "createdBy", ignore = true),
          @Mapping(target = "updatedAt", ignore = true),
          @Mapping(target = "updatedBy", ignore = true),
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "syncedAt", ignore = true),
          @Mapping(target = "isForeignDeposit", source = "account.foreignDeposit")
      }
  )
  void merge(ExecutionContext context, Account account, @MappingTarget AccountListEntity entity);

  @Mappings(
      value = {@Mapping(target = "foreignDeposit", source = "isForeignDeposit")}
  )
  Account entityToDto(AccountListEntity entity);
}
