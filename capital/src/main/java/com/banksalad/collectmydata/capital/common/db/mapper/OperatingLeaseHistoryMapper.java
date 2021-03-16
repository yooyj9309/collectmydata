package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperatingLeaseHistoryMapper {

  OperatingLeaseHistoryEntity toOperatingLeaseHistoryEntity(OperatingLeaseEntity operatingLeaseEntity);
}
