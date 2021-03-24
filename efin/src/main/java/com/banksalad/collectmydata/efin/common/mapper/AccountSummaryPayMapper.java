package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryPayEntity;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummaryPay;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountSummaryPayMapper {

  AccountSummaryPay entityToDto(AccountSummaryPayEntity accountSummaryPayEntity);

  AccountSummaryPayEntity dtoToEntity(AccountSummaryPay accountSummaryPay);
}
