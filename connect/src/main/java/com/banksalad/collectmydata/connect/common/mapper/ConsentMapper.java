package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.ConsentEntity;
import com.banksalad.collectmydata.connect.common.dto.Consent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface ConsentMapper {

  @Mapping(target = "id", ignore = true)
  ConsentEntity dtoToEntity(Consent consent, @MappingTarget ConsentEntity consentEntity);

  Consent entityToDto(ConsentEntity consentEntity);
}
