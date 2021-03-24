package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.ServiceEntity;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface FinanceServiceMapper {

  @Mappings(
      value = {
          @Mapping(target = "id", ignore = true),
      }
  )
  void merge(FinanceOrganizationServiceInfo serviceInfo, @MappingTarget ServiceEntity serviceEntity);
}
