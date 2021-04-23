package com.banksalad.collectmydata.common.grpc.converter;


import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.NumberUtil;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;

import java.math.BigDecimal;
import java.util.Optional;

public class ProtoTypeConverter {

  public static StringValue toStringValue(String source) {
    return Optional.ofNullable(source)
        .map(StringValue::of)
        .orElse(StringValue.getDefaultInstance());
  }

  public static Int64Value toInt64Value(BigDecimal source) {
    return Optional.ofNullable(source)
        .map(BigDecimal::longValueExact)
        .map(Int64Value::of)
        .orElse(Int64Value.getDefaultInstance());
  }

  public static Int64Value toInt64ValueMultiply1000(BigDecimal source) {
    return Optional.ofNullable(source)
        .map(NumberUtil::multiply1000)
        .map(Int64Value::of)
        .orElse(Int64Value.getDefaultInstance());
  }

  public static Int32Value toInt32Value(Integer source) {
    return Optional.ofNullable(source)
        .map(Int32Value::of)
        .orElse(Int32Value.getDefaultInstance());
  }

  public static Int64Value toDateEpochMilli(String source) {
    if (source == null || source.isBlank()) {
      return Int64Value.getDefaultInstance();
    }

    return Int64Value.of(DateUtil.toDateEpochMilli(source));
  }

  public static Int64Value toDatetimeEpochMilli(String source) {
    if (source == null || source.isBlank()) {
      return Int64Value.getDefaultInstance();
    }

    return Int64Value.of(DateUtil.toDatetimeEpochMilli(source));
  }

  public static BoolValue toBoolValue(Boolean source) {
    return Optional.ofNullable(source)
        .map(BoolValue::of)
        .orElse(BoolValue.getDefaultInstance());
  }
}
