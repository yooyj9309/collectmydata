package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseHistoryEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {BigDecimalMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperatingLeaseHistoryMapper {

  OperatingLeaseHistoryEntity toHistoryEntity(OperatingLeaseEntity operatingLeaseEntity);
}
