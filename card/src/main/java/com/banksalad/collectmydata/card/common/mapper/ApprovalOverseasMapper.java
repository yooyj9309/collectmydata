package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.card.dto.ApprovalOverseas;
import com.banksalad.collectmydata.card.common.db.entity.ApprovalOverseasEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface ApprovalOverseasMapper {

  @Mappings(
      value = {
          @Mapping(target = "approvedAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "krwAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  ApprovalOverseasEntity dtoToEntity(ApprovalOverseas approvalOverseas);
}
