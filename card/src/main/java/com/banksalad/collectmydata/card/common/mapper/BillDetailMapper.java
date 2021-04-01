package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.card.dto.BillDetail;
import com.banksalad.collectmydata.card.common.db.entity.BillDetailEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface BillDetailMapper {

  @Mappings(
      value = {
          @Mapping(target = "paidAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(source = "currencyCode", target = "currencyCode", defaultValue = "KRW"),
          @Mapping(target = "creditFeeAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "totalInstallCnt", expression = "java(billDetail.getTotalInstallCnt() < 1? null:billDetail.getTotalInstallCnt())"),
          @Mapping(target = "curInstallCnt", expression = "java(billDetail.getCurInstallCnt() < 1? null:billDetail.getCurInstallCnt())"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  BillDetailEntity dtoToEntity(BillDetail billDetail);
}
