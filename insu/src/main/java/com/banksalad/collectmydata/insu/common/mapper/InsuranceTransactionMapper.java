package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceTransactionEntity;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceTransactionMapper {

  void merge(InsuranceTransactionEntity source, @MappingTarget InsuranceTransactionEntity target);
}
