package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationEntity;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public abstract class OrganizationMapper {

  @Mappings(
      value = {
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "sector", ignore = true),
          @Mapping(target = "industry", ignore = true),
          @Mapping(target = "organizationId", ignore = true),
      }
  )
  public abstract void mergeDtoToEntity(FinanceOrganizationInfo organizationResponse,
      @MappingTarget OrganizationEntity entity);

  @AfterMapping
  public void defaultSetting(@MappingTarget OrganizationEntity entity) {
    if (entity.getId() == null) {
      entity.setSector(MydataSector.UNKNOWN.name());
      entity.setIndustry(Industry.UNKNOWN.name());
    }
  }
}
