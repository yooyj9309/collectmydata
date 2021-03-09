package com.banksalad.collectmydata.capital.common.db.entity.mapper;

import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

// IGNORE policy suppresses complaining the source fields do not exist in the target.
@Mapper(uses = {BigDecimalMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountTransactionMapper {

  // Update only non-null fields partially with the below @BeanMapping.
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mappings(value = {
      // Data types of `transDtime` between two are quite different so advice an explicit formatting.
      // `syncedAt' is copied smoothly without any directives.
      @Mapping(source = "accountTransaction.transDtime", target = "transDtime", dateFormat = "yyyyMMddHHmmss")
  })
  void updateEntityFromDto(AccountTransaction accountTransaction,
      @MappingTarget AccountTransactionEntity accountTransactionEntity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mappings(value = {
      @Mapping(target = "intCnt", ignore = true)
  })
  void updateDtoFromDto(AccountTransaction sourceAccountTransaction,
      @MappingTarget AccountTransaction targetAccountTransaction);
}
