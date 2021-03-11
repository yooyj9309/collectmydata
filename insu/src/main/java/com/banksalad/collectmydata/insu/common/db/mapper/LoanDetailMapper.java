package com.banksalad.collectmydata.insu.common.db.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailEntity;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanDetailMapper {

  LoanDetail toDto(LoanDetailEntity entity);

  LoanDetailEntity toEntity(LoanDetail loanDetail);
}
