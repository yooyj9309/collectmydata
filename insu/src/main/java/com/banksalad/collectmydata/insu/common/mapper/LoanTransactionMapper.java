package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionEntity;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanTransactionPublishmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanTransactionMapper {

  LoanTransactionEntity dtoToEntity(LoanTransaction loanTransaction);

  LoanTransactionPublishmentResponse entityToPublishmentDto(LoanTransactionEntity loanTransactionEntity);
}
