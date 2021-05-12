package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionInterestEntity;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanTransactionInterestPublishmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanTransactionInterestMapper {

  LoanTransactionInterestPublishmentResponse entityToPublishmentDto(
      LoanTransactionInterestEntity loanTransactionInterestEntity);
}
