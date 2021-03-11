package com.banksalad.collectmydata.insu.common.db.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface LoanSummaryMapper {

  void merge(LoanSummary loanSummary, @MappingTarget LoanSummaryEntity loanSummaryEntity);

  LoanSummary entityToDto(LoanSummaryEntity entity);
}
