package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.ServiceEntity;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ServiceMapper {

  void mergeDtoToEntity(FinanceOrganizationServiceInfo serviceInfo, @MappingTarget ServiceEntity serviceEntity);
}
