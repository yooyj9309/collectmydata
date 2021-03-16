package com.banksalad.collectmydata.capital.common.db.mapper;

import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

// This set the scale of BigDecimal values of the target (database entity) objects.
@Mapper
public abstract class BigDecimalMapper {

  private static final int AMOUNT_SCALE = 3;

  BigDecimal setScale(BigDecimal amount) {
    return amount.setScale(AMOUNT_SCALE, RoundingMode.DOWN);
  }
}
