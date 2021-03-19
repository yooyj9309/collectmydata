package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountSummaryMapper {

  @Mappings(value = {
      @Mapping(target = "basicSearchTimestamp", ignore = true),
      @Mapping(target = "detailSearchTimestamp", ignore = true),
      @Mapping(target = "operatingLeaseBasicSearchTimestamp", ignore = true)
  })
  void mergeDtoToEntity(AccountSummary accountSummary, @MappingTarget AccountSummaryEntity entity);

  AccountSummary entityToDto(AccountSummaryEntity entity);
}
