package com.banksalad.collectmydata.capital.common.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseBasicEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseBasicHistoryEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {BigDecimalMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperatingLeaseHistoryMapper {

  OperatingLeaseBasicHistoryEntity toHistoryEntity(OperatingLeaseBasicEntity operatingLeaseBasicEntity);
}
