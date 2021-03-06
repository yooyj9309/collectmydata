package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermEntity;
import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface LoanShortTermHistoryMapper {

  @Mapping(target = "id", ignore = true)
  LoanShortTermHistoryEntity toHistoryEntity(LoanShortTermEntity loanShortTermEntity, @MappingTarget LoanShortTermHistoryEntity loanShortTermHistoryEntity);
}
