package com.banksalad.collectmydata.insu.common.db.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionEntity;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanTransactionMapper {

  LoanTransactionEntity toLoanTransactionEntityFrom(LoanTransaction loanTransaction);
}
