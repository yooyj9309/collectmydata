package com.banksalad.collectmydata.connect.token.dto;

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
public class ExternalIssueTokenRequest {

  private String orgCode;

  @Builder.Default
  private String grantType = "authorization_code";

  private String code;
  private String clientId;
  private String clientSecret;
  private String redirectUri;
}
