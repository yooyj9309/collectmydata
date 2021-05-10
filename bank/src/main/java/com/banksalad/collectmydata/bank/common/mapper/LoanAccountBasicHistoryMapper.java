package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicHistoryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface LoanAccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  LoanAccountBasicHistoryEntity entityToHistoryEntity(LoanAccountBasicEntity loanAccountBasicEntity,
      @MappingTarget LoanAccountBasicHistoryEntity loanAccountBasicHistoryEntity);
}
