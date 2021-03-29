package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceBasicMapper {

  InsuranceBasicEntity dtoToEntity(InsuranceBasic insuranceBasic);
}
