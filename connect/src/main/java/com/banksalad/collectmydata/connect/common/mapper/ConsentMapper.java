package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.ConsentEntity;
import com.banksalad.collectmydata.connect.common.dto.Consent;
import org.mapstruct.Mapper;

@Mapper
public interface ConsentMapper {

  ConsentEntity dtoToEntity(Consent consent);

  Consent entityToDto(ConsentEntity consentEntity);
}
