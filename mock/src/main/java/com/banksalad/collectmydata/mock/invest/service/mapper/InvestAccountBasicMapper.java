package com.banksalad.collectmydata.mock.invest.service.mapper;

import com.banksalad.collectmydata.mock.common.db.entity.InvestAccountBasicEntity;
import com.banksalad.collectmydata.mock.common.db.entity.InvestAccountSummaryEntity;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvestAccountBasicMapper {

  InvestAccountBasic entityToDto(InvestAccountBasicEntity investAccountBasicEntity);
}
