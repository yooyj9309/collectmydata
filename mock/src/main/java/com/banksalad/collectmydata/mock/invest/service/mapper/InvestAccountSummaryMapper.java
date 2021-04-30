package com.banksalad.collectmydata.mock.invest.service.mapper;

import com.banksalad.collectmydata.mock.common.db.entity.InvestAccountSummaryEntity;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvestAccountSummaryMapper {

  @Mapping(target = "isConsent", constant = "true")
  InvestAccountSummary entityToDto(InvestAccountSummaryEntity accountSummaryEntity);
}
