package com.banksalad.collectmydata.irp.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IrpAccountSummaryMapper {

  @Mappings(
      value = {
          @Mapping(target = "isConsent", source = "consent"),
      }
  )
  void merge(IrpAccountSummary accountSummary, @MappingTarget IrpAccountSummaryEntity entity);

  @Mappings(
      value = {
          @Mapping(target = "consent", source = "isConsent"),
      }
  )
  IrpAccountSummary entityToDto(IrpAccountSummaryEntity entity);
}
