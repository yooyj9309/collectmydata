package com.banksalad.collectmydata.card.publishment.transaction.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApprovalDomesticPublishment {

  private String cardId;

  private String approvedNum;

  private String status;

  private String payType;

  private String approvedDtime;

  private String cancelDtime;

  private String merchantName;

  private BigDecimal approvedAmt;

  private Integer totalInstallCnt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
