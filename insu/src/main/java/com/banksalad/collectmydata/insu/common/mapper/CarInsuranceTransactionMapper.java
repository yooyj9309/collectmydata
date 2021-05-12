package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceTransactionEntity;
import com.banksalad.collectmydata.insu.publishment.car.dto.CarInsuranceTransactionPublishmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface CarInsuranceTransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "faceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "paidAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  CarInsuranceTransactionEntity dtoToEntity(CarInsuranceTransaction carInsuranceTransaction);

  CarInsuranceTransactionPublishmentResponse entityToPublishmentDto(
      CarInsuranceTransactionEntity carInsuranceTransactionEntity);
}
