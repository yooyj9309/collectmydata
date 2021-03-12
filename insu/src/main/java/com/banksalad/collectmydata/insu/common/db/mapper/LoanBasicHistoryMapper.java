package com.banksalad.collectmydata.insu.common.db.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LoanBasicHistoryMapper {


  @Mapping(target = "id", ignore = true)
  LoanBasicHistoryEntity toHistoryEntity(LoanBasicEntity entity);
}
