package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.card.loan.dto.LoanSummary;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanSummaryPublishment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanSummaryMapper {

  LoanSummaryEntity dtoToEntity(LoanSummary loanSummary, @MappingTarget LoanSummaryEntity loanSummaryEntity);

  LoanSummary entityToDto(LoanSummaryEntity loanSummaryEntity);

  LoanSummaryPublishment entityToPublishmentDto(LoanSummaryEntity loanSummaryEntity);
}
