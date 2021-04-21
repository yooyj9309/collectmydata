package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicEntity;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountBasicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvestAccountBasicMapper {

  InvestAccountBasicEntity dtoToEntity(InvestAccountBasic investAccountBasic);

  InvestAccountBasicResponse entityToResponseDto(InvestAccountBasicEntity investAccountBasicEntity);
}
