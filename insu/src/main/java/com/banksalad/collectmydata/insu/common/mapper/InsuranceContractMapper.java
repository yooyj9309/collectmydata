package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractEntity;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceContractPublishmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceContractMapper {

  InsuranceContractEntity dtoToEntity(InsuranceContract insuranceContract);

  InsuranceContract entityToDto(InsuranceContractEntity entity);

  InsuranceContractPublishmentResponse entityToPublishmentDto(InsuranceContractEntity insuranceContractEntity);
}
