package com.banksalad.collectmydata.irp.common.dto;

import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IrpAccountBasicResponse implements AccountResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;

  @JsonUnwrapped
  private IrpAccountBasic irpAccountBasic;
}
