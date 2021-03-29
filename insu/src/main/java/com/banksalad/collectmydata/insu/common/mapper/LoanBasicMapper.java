package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanBasicMapper {

  LoanBasic entityToDto(LoanBasicEntity entity);
}
