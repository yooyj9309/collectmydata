package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.card.dto.ApprovalDomestic;
import com.banksalad.collectmydata.card.common.db.entity.ApprovalDomesticEntity;
import com.banksalad.collectmydata.card.publishment.transaction.dto.ApprovalDomesticPublishment;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface ApprovalDomesticMapper {

  @Mappings(
      value = {
          @Mapping(target = "approvedAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  ApprovalDomesticEntity dtoToEntity(ApprovalDomestic approvalDomestic);

  ApprovalDomestic entityToDto(ApprovalDomesticEntity approvalDomesticEntity);

  ApprovalDomesticPublishment entityToPublishmentDto(ApprovalDomesticEntity approvalDomesticEntity);
}
