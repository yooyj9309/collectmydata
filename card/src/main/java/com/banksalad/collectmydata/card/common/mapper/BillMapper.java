package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.common.db.entity.BillEntity;
import com.banksalad.collectmydata.card.publishment.bill.dto.BillBasicPublishment;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface BillMapper {

  @Mappings(
      value = {
          @Mapping(target = "chargeAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  BillBasic entityToDto(BillEntity billEntity);

  @Mappings(
      value = {
          @Mapping(target = "chargeAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  BillEntity dtoToEntity(BillBasic billBasic);

  BillBasicPublishment entityToPublishmentDto(BillEntity billEntity);
}
