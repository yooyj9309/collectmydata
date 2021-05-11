package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.publishment.organization.dto.OrganizationForFinance;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import static com.banksalad.collectmydata.connect.common.constant.OrganizationConstants.AUTH_URI;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConnectOrganizationMapper {

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "domain", target = "authUrl", qualifiedByName = "domainToAuthUrl")
  OrganizationForFinance entityToDto(ConnectOrganizationEntity connectOrganizationEntity);

  @Named("domainToAuthUrl")
  static String domainToAuthUrl(String domain) {
    return domain + AUTH_URI;
  }
}
