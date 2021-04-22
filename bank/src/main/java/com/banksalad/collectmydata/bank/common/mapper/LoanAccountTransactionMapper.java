package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionEntity;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionInterest;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionResponse;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanAccountTransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "principalAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "intAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  LoanAccountTransactionEntity dtoToEntity(LoanAccountTransaction loanAccountTransaction);

  @Mapping(target = "loanAccountTransactionInterests", source = "loanAccountTransactionInterests")
  LoanAccountTransactionResponse entityToResponseDto(LoanAccountTransactionEntity loanAccountTransactionEntity,
      List<LoanAccountTransactionInterest> loanAccountTransactionInterests);
}
