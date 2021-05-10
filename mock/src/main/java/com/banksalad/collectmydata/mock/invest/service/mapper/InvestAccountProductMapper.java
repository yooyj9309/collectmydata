package com.banksalad.collectmydata.mock.invest.service.mapper;

import com.banksalad.collectmydata.mock.common.db.entity.InvestAccountProductEntity;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountProduct;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvestAccountProductMapper {

  InvestAccountProduct entityToDto(InvestAccountProductEntity investAccountProductEntity);
}
