package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountSummaryMapper {

  void mergeDtoToEntity(AccountSummary accountSummary, @MappingTarget AccountSummaryEntity entity);

  AccountSummary entityToDto(AccountSummaryEntity entity);
}
