package com.banksalad.collectmydata.irp.common.dto;

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
public class IrpAccountDetailsResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;
  private String nextPage;

  private int irpCnt;

  @JsonProperty(value = "irp_list")
  @Builder.Default
  private List<IrpAccountDetail> irpAccountDetails = new ArrayList<>();
}
