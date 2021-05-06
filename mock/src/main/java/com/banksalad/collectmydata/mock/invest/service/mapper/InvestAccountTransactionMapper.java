package com.banksalad.collectmydata.mock.invest.service.mapper;

import com.banksalad.collectmydata.mock.common.db.entity.InvestAccountTransactionEntity;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvestAccountTransactionMapper {

  InvestAccountTransaction entityToDto(InvestAccountTransactionEntity investAccountTransactionEntity);
}
