package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountSummaryMapper {

  void merge(AccountSummary accountSummary, @MappingTarget AccountSummaryEntity entity);

  AccountSummary entityToDto(AccountSummaryEntity entity);
}
