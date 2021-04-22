package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountDetailEntity;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountDetail;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountDetailResponse;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanAccountDetailMapper {

  @Mappings(
      value = {
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "loanPrincipal", qualifiedByName = "BigDecimalScale3")
      }
  )
  LoanAccountDetailEntity dtoToEntity(LoanAccountDetail loanAccountDetail);

  LoanAccountDetailResponse entityToResponseDto(LoanAccountDetailEntity loanAccountDetailEntity);
}
