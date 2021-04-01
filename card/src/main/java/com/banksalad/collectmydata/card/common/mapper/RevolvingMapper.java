package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.card.dto.Revolving;
import com.banksalad.collectmydata.card.common.db.entity.RevolvingEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface RevolvingMapper {

  @Mappings(
      value = {
          @Mapping(target = "minPayRate", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "minPayAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "agreedPayRate", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "remainedAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  RevolvingEntity dtoToEntity(Revolving revolving);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mappings(
      value = {
          @Mapping(target = "minPayRate", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "minPayAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "agreedPayRate", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "remainedAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  Revolving entityToDto(RevolvingEntity revolvingEntity);
}
