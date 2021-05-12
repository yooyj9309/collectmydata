package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;
import com.banksalad.collectmydata.insu.publishment.car.dto.CarInsurancePublishmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarInsuranceMapper {

  @Mappings(
      value = {
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "syncedAt", ignore = true),
          @Mapping(target = "banksaladUserId", ignore = true),
          @Mapping(target = "organizationId", ignore = true),
          @Mapping(target = "insuNum", ignore = true),
          @Mapping(target = "transactionSyncedAt", ignore = true),
          @Mapping(target = "transactionResponseCode", ignore = true),
          @Mapping(target = "selfPayAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  CarInsuranceEntity dtoToEntity(CarInsurance carInsurance);

  CarInsurance entityToDto(CarInsuranceEntity carInsuranceEntity);

  CarInsurancePublishmentResponse entityToPublishmentDto(CarInsuranceEntity carInsuranceEntity);
}
