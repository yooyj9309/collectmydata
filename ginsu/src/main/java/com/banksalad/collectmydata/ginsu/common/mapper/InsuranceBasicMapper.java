package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.ginsu.insurance.dto.InsuranceBasic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface InsuranceBasicMapper {

  @Mappings(
      value = {
          @Mapping(target = "faceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "payAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  InsuranceBasicEntity dtoToEntity(InsuranceBasic insuranceBasic);

}
