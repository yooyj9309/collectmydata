package com.banksalad.collectmydata.mock.common.api.context;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseWrapper<T> {

  private String rspCode;
  private String rspMsg;
  private Long searchTimestamp;

  @JsonUnwrapped
  private T data;

  public ApiResponseWrapper<T> setResponseSearchTimestamp() {
    searchTimestamp = NumberUtils.toLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    return this;
  }
}
