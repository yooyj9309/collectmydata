package com.banksalad.collectmydata.connect.common.db.entity.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.FinanceServiceClientIpEntity;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceIp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface FinanceServiceClientIpMapper {

  @Mappings(
      value = {
          @Mapping(target = "serviceClientIpId", ignore = true),
      }
  )
  void merge(FinanceOrganizationServiceIp serviceIp,
      @MappingTarget FinanceServiceClientIpEntity serviceClientIpEntity);
}
