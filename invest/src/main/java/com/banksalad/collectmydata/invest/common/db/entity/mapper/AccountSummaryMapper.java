package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountSummaryMapper {

  void merge(AccountSummary accountSummary, @MappingTarget AccountSummaryEntity accountSummaryEntity);

  AccountSummary entityToDto(AccountSummaryEntity accountSummaryEntity);
}
