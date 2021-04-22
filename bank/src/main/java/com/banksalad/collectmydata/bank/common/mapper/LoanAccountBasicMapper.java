package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountBasic;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountBasicResponse;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanAccountBasicMapper {

  @Mappings(
      value = {
          @Mapping(target = "lastOfferedRate", qualifiedByName = "BigDecimalScale3")
      }
  )
  LoanAccountBasicEntity dtoToEntity(LoanAccountBasic loanAccountBasic);

  LoanAccountBasicResponse entityToResponseDto(LoanAccountBasicEntity loanAccountBasicEntity);
}
