package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceTransactionEntity;

import com.banksalad.collectmydata.insu.insurance.dto.InsuranceTransaction;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceTransactionPublishmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface InsuranceTransactionMapper {

  @Mapping(target = "paidAmt", qualifiedByName = "BigDecimalScale3")
  InsuranceTransactionEntity dtoToEntity(InsuranceTransaction insuranceTransaction);

  InsuranceTransactionPublishmentResponse entityToPublishmentDto(InsuranceTransactionEntity insuranceTransactionEntity);
}
