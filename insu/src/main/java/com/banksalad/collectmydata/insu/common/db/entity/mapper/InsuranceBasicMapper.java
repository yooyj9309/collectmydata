package com.banksalad.collectmydata.insu.common.db.entity.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicResponse;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceBasicMapper {

  GetInsuranceBasicResponse entityToResponseDto(InsuranceBasicEntity entity);

  @Mappings(
      value = {
          @Mapping(target = "issueDate", dateFormat = "yyyyMMdd"),
          @Mapping(target = "expDate", dateFormat = "yyyyMMdd"),
          @Mapping(target = "pensionRcvStartDate", dateFormat = "yyyyMMdd")
      }
  )
  void merge(GetInsuranceBasicResponse response, @MappingTarget InsuranceBasicEntity entity);

  InsuranceBasic responseDtoToDto(GetInsuranceBasicResponse response);
}
