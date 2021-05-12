package com.banksalad.collectmydata.insu.publishment.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InsuranceSummaryPublishmentResponse {

  private String insuNum;

  @JsonProperty("is_consent")
  private boolean consent;

  private String insuType;

  private String prodName;

  private String insuStatus;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
