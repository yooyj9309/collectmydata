package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.card.dto.CardBasic;
import com.banksalad.collectmydata.card.card.dto.Point;
import com.banksalad.collectmydata.card.common.db.entity.CardEntity;
import com.banksalad.collectmydata.card.common.db.entity.PointEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface PointMapper {

  @Mappings(
      value = {
          @Mapping(target = "remainPointAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "expiringPointAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  PointEntity dtoToEntity(Point point);
}
