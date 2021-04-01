package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermEntity;
import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LoanLongTermHistoryMapper {

  @Mapping(target = "id", ignore = true)
  LoanLongTermHistoryEntity toHistoryEntity(LoanLongTermEntity loanLongTermEntity);
}
