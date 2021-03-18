package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import org.mapstruct.Mapper;

@Mapper
public interface LoanBasicMapper {

  LoanBasicEntity responseDtoToEntity(GetLoanBasicResponse response);

  LoanBasic entityToDto(LoanBasicEntity entity);

}
