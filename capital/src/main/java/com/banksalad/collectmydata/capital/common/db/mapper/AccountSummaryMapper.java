package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface AccountSummaryMapper {

  @Mappings(
      value = {
          @Mapping(target = "basicSearchTimestamp", ignore = true),
          @Mapping(target = "detailSearchTimestamp", ignore = true),
          @Mapping(target = "operatingLeaseBasicSearchTimestamp", ignore = true)
      }
  )
  void mergeDtoToEntity(AccountSummary accountSummary, @MappingTarget AccountSummaryEntity entity);

  AccountSummary entityToDto(AccountSummaryEntity entity);
}
