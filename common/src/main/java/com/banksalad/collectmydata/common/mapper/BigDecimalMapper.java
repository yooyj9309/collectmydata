package com.banksalad.collectmydata.common.mapper;

import com.banksalad.collectmydata.common.util.NumberUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper
public abstract class BigDecimalMapper {

  @Named("BigDecimalScale3")
  public BigDecimal convertBigDecimalScale3(BigDecimal bigDecimal) {
    return NumberUtil.setScale(bigDecimal, 3);
  }

  @Named("BigDecimalScale5")
  public BigDecimal convertBigDecimalScale5(BigDecimal bigDecimal) {
    return NumberUtil.setScale(bigDecimal, 5);
  }
}
