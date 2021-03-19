package com.banksalad.collectmydata.capital.account.dto;

import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetAccountDetailResponse implements AccountResponse {

  private String rspCode;

  private String rspMsg;

  private long searchTimestamp;

  @JsonUnwrapped
  private AccountDetail accountDetail;
}
