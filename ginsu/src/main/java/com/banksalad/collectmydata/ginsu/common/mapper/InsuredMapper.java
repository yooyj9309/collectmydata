package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.ginsu.insurance.dto.Insured;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuredMapper {

  InsuredEntity dtoToEntity(Insured insured);

  Insured entityToDto(InsuredEntity insuredEntity);
}
