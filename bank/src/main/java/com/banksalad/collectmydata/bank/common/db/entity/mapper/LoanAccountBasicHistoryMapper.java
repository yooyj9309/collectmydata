package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LoanAccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  LoanAccountBasicHistoryEntity toLoanAccountBasicHistoryEntity(LoanAccountBasicEntity loanAccountBasicEntity);
}
