package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionInterestEntity;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountTransactionInterest;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanAccountTransactionInterestMapper {

  @Mappings(
      value = {
          @Mapping(target = "intRate", qualifiedByName = "BigDecimalScale3")
      }
  )
  LoanAccountTransactionInterestEntity dtoToEntity(LoanAccountTransactionInterest loanAccountTransactionInterest);
}
