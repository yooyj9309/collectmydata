package com.banksalad.collectmydata.insu.common.db.entity.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuredMapper {

  Insured entityToDto(InsuredEntity entity);

  void merge(Insured insured, @MappingTarget InsuredEntity insuredEntity);
}
