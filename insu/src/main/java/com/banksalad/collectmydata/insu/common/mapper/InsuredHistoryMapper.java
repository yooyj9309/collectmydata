package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuredHistoryMapper {

  @Mapping(target = "id", ignore = true)
  InsuredHistoryEntity entityToHistoryEntity(InsuredEntity insuredEntity,
      @MappingTarget InsuredHistoryEntity insuredHistoryEntity);
}
