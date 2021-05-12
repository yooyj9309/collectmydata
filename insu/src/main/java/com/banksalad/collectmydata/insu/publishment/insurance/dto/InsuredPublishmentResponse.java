package com.banksalad.collectmydata.insu.publishment.insurance.dto;

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
public class InsuredPublishmentResponse {

  private String insuNum;

  private String insuredNo;

  private String insuredName;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}

