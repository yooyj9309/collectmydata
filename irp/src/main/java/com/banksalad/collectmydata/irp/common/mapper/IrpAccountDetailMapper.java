package com.banksalad.collectmydata.irp.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IrpAccountDetailMapper {

  @Mappings(
      value = {
          @Mapping(target = "evalAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "invPrincipal", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "intRate", qualifiedByName = "BigDecimalScale3"),
      }
  )
  IrpAccountDetail entityToDto(IrpAccountDetailEntity irpAccountDetailEntity);

  @Mappings(
      value = {
          @Mapping(target = "evalAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "invPrincipal", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "intRate", qualifiedByName = "BigDecimalScale5"),
      }
  )
  IrpAccountDetailEntity dtoToEntity(IrpAccountDetail irpAccountDetail);
}
