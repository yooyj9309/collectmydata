package com.banksalad.collectmydata.capital.common.db.entity.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface OperatingLeaseHistoryMapper {

  @Mappings(
      value = {
          @Mapping(target = "operatingLeaseHistoryId", ignore = true),
      }
  )
  public OperatingLeaseHistoryEntity toOperatingLeaseHistoryEntity(OperatingLeaseEntity operatingLeaseEntity);
}
