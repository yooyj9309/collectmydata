package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.card.dto.Point;
import com.banksalad.collectmydata.card.common.db.entity.PointEntity;
import com.banksalad.collectmydata.card.publishment.userbase.dto.PointPublishment;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface PointMapper {

  PointEntity dtoToEntity(Point point);

  Point entityToDto(PointEntity pointEntity);

  PointPublishment entityToPublishmentDto(PointEntity pointEntity);
}
