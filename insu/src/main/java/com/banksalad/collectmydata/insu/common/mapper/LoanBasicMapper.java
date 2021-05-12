package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;

import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanBasicPublishmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanBasicMapper {

  LoanBasic entityToDto(LoanBasicEntity entity);

  LoanBasicPublishmentResponse entityToPublishmentDto(LoanBasicEntity loanBasicEntity);
}
