package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.efin.account.dto.AccountTransaction;
import com.banksalad.collectmydata.efin.common.db.entity.AccountTransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface AccountTransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "totalInstallCnt", expression = "java(accountTransaction.getTotalInstallCnt() < 1? null:accountTransaction.getTotalInstallCnt())"),
      }
  )
  AccountTransactionEntity dtoToEntity(AccountTransaction accountTransaction);
}
