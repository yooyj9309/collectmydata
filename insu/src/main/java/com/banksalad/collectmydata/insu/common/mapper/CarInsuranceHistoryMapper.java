package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarInsuranceHistoryMapper {

  @Mapping(target = "id", ignore = true)
  CarInsuranceHistoryEntity entityToHistoryEntity(CarInsuranceEntity carInsuranceEntity,
      @MappingTarget CarInsuranceHistoryEntity carInsuranceHistoryEntity);
}
