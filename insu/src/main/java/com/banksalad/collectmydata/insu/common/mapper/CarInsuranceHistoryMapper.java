package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CarInsuranceHistoryMapper {

  @Mapping(target = "id", ignore = true)
  CarInsuranceHistoryEntity toHistoryEntity(CarInsuranceEntity carInsuranceEntity);
}
