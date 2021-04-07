package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.OrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrganizationHistoryMapper {

  @Mapping(target = "id", ignore = true)
  OrganizationHistoryEntity toHistoryEntity(OrganizationEntity organizationEntity);
}
