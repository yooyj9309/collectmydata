package com.banksalad.collectmydata.insu.common.db.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceTransactionEntity;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface InsuranceTransactionMapper {

  void merge(InsuranceTransaction insuranceTransaction, @MappingTarget InsuranceTransactionEntity entity);
}
