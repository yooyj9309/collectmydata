package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  LoanBasicHistoryEntity entityToHistoryEntity(LoanBasicEntity loanBasicEntity,
      @MappingTarget LoanBasicHistoryEntity loanBasicHistoryEntity);
}
