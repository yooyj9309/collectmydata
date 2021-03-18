package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractEntity;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceContractMapper {

  InsuranceContractEntity dtoToEntity(InsuranceContract insuranceContract);

  @Mappings(
      value = {
          @Mapping(target = "insuNum", ignore = true),
          @Mapping(target = "insuredNo", ignore = true)
      }
  )
  InsuranceContract entityToDto(InsuranceContractEntity entity);
}
