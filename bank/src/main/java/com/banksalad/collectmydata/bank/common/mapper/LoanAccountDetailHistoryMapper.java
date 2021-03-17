package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LoanAccountDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  LoanAccountDetailHistoryEntity toLoanAccountDetailHistoryEntity(LoanAccountDetailEntity loanAccountDetailEntity);
}
