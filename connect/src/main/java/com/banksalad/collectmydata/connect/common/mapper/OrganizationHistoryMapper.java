package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.OrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface OrganizationHistoryMapper {

  @Mapping(target = "id", ignore = true)
  OrganizationHistoryEntity toHistoryEntity(OrganizationEntity organizationEntity,
      @MappingTarget OrganizationHistoryEntity organizationHistoryEntity);
}
