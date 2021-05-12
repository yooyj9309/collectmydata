package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailEntity;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanDetailPublishmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanDetailMapper {

  LoanDetailPublishmentResponse entityToPublishmentDto(LoanDetailEntity loanDetailEntity);
}
