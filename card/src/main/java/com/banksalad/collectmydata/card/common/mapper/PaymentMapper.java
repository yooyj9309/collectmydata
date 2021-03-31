package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.card.dto.Payment;
import com.banksalad.collectmydata.card.common.db.entity.PaymentEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface PaymentMapper {
  @Mappings(
      value = {
          @Mapping(target = "payAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  PaymentEntity dtoToEntity(Payment payment);
}
