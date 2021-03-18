package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionInterestEntity;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransactionInterest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanTransactionInterestMapper {

  LoanTransactionInterestEntity toLoanTransactionInterestEntityFrom(LoanTransactionInterest loanTransactionInterest);
}
