package com.banksalad.collectmydata.efin.account.dto;

import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ListAccountBalancesResponse implements AccountResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;
  private int fobCnt;

  @JsonProperty(value = "fob_list")
  @Builder.Default
  private List<AccountBalance> accountBalances = new ArrayList<>();
}
