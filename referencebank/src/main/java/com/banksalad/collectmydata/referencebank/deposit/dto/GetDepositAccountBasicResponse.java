package com.banksalad.collectmydata.referencebank.deposit.dto;

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
public class GetDepositAccountBasicResponse implements AccountResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;

  @JsonUnwrapped
  private DepositAccountBasic depositAccountBasic;
}
