package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuredPublishmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuredMapper {

  InsuredEntity dtoToEntity(Insured insured);

  Insured entityToDto(InsuredEntity insuredEntity);

  InsuredPublishmentResponse entityToPublishmentDto(InsuredEntity insuredEntity);
}
