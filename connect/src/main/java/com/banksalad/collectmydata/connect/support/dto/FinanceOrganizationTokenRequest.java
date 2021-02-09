package com.banksalad.collectmydata.connect.support.dto;

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
public class FinanceOrganizationTokenRequest {

  @Builder.Default
  private String grantType = "client_credentials";
  private String clientId;
  private String clientSecret;
  @Builder.Default
  private String scope = "manage";
}
