package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuredMapper {

  InsuredEntity dtoToEntity(Insured insured);

  Insured entityToDto(InsuredEntity insuredEntity);
}
