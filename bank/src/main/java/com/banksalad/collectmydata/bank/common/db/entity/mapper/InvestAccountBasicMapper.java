package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicEntity;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvestAccountBasicMapper {

  InvestAccountBasic entityToDto(InvestAccountBasicEntity entity);

  @Mappings(
      value = {
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "banksaladUserId", ignore = true),
          @Mapping(target = "organizationId", ignore = true),
          @Mapping(target = "accountNum", ignore = true),
          @Mapping(target = "seqno", ignore = true),
          @Mapping(target = "syncedAt", ignore = true),
          @Mapping(target = "issueDate", source = "issueDate", dateFormat = "yyyyMMdd"),
          @Mapping(target = "expDate", source = "expDate", dateFormat = "yyyyMMdd")
      }
  )
  InvestAccountBasicEntity dtoToEntity(InvestAccountBasic investAccountBasic);
}
