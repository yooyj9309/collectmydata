package com.banksalad.collectmydata.irp.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IrpAccountSummary {

  private String accountNum;

  private String seqno;

  @JsonProperty("is_consent")
  private boolean consent;

  private String prodName;

  private String accountStatus;

  @JsonIgnore
  private long basicSearchTimestamp;

  @JsonIgnore
  private long detailSearchTimestamp;

  @JsonIgnore
  private LocalDate transactionSyncedAt;

  @JsonIgnore
  private String basicResponseCode;

  @JsonIgnore
  private String detailResponseCode;

  @JsonIgnore
  private String transactionResponseCode;
}
