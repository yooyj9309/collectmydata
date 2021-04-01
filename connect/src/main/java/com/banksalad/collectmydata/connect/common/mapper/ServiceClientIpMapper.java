package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.ServiceClientIpEntity;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceIp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ServiceClientIpMapper {

  void mergeDtoToEntity(FinanceOrganizationServiceIp serviceIp,
      @MappingTarget ServiceClientIpEntity serviceClientIpEntity);
}
