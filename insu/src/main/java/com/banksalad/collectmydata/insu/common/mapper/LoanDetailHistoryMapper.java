package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  LoanDetailHistoryEntity toEntity(LoanDetailEntity entity);
}
