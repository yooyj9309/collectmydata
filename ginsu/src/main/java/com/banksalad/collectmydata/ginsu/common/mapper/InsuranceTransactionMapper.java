package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceTransactionEntity;
import com.banksalad.collectmydata.ginsu.insurance.dto.InsuranceTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceTransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "paidAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  InsuranceTransactionEntity dtoToEntity(InsuranceTransaction insuranceTransaction);
}
