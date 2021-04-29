package com.banksalad.collectmydata.irp.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IrpAccountBasicMapper {

  @Mappings(value = {
      @Mapping(target = "accumAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "evalAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "employerAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "employeeAmt", qualifiedByName = "BigDecimalScale3")
  })
  IrpAccountBasic entityToDto(IrpAccountBasicEntity entity);

  @Mappings(
      value = {
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "banksaladUserId", ignore = true),
          @Mapping(target = "organizationId", ignore = true),
          @Mapping(target = "accountNum", ignore = true),
          @Mapping(target = "seqno", ignore = true),
          @Mapping(target = "syncedAt", ignore = true),
          @Mapping(target = "accumAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "evalAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "employerAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "employeeAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  IrpAccountBasicEntity dtoToEntity(IrpAccountBasic irpAccountBasic);
}
