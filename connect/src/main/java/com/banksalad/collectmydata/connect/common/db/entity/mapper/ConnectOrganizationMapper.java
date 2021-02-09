package com.banksalad.collectmydata.connect.common.db.entity.mapper;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationInfo;
import io.netty.util.internal.StringUtil;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public abstract class ConnectOrganizationMapper {

  @Mappings(
      value = {
          @Mapping(target = "connectOrganizationId", ignore = true),
          @Mapping(target = "sector", ignore = true),
          @Mapping(target = "industry", ignore = true),
          @Mapping(target = "organizationId", ignore = true),
          @Mapping(target = "organizationObjectid", ignore = true),
          @Mapping(target = "organizationStatus", ignore = true),
          @Mapping(target = "isRelayOrganization", ignore = true),
          @Mapping(source = "orgCode", target = "organizationCode")
      }
  )
  public abstract void merge(FinanceOrganizationInfo organizationResponse,
      @MappingTarget ConnectOrganizationEntity entity);
  
  @AfterMapping
  public void defaultSetting(
      @MappingTarget ConnectOrganizationEntity entity) {
    if (entity.getConnectOrganizationId() == null) {
      entity.setSector(MydataSector.UNKNOWN.name());
      entity.setIndustry(Industry.UNKNOWN.name());
      entity.setOrganizationId(entity.getOrganizationCode());  // 임시값
      entity.setOrganizationObjectid(entity.getOrganizationCode()); // 임시값
      entity.setOrganizationStatus(""); // 현재 사용하지 않는 필드
    }
    boolean isRelayOrganization = StringUtil.isNullOrEmpty(entity.getRelayOrgCode()) ? false : true;
    entity.setIsRelayOrganization(isRelayOrganization);

  }
}
