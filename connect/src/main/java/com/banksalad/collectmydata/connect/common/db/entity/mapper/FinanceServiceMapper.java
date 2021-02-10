package com.banksalad.collectmydata.connect.common.db.entity.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.FinanceServiceEntity;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface FinanceServiceMapper {

  @Mappings(
      value = {
          @Mapping(target = "serviceId", ignore = true),
      }
  )
  void merge(FinanceOrganizationServiceInfo serviceInfo, @MappingTarget FinanceServiceEntity financeServiceEntity);
}
