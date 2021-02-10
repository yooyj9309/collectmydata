package com.banksalad.collectmydata.connect.support.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FinanceOrganizationServiceInfo {

  private String serviceName; // 서비스명
  private String opType; // 서비스 정보의 구분 신규/수정/삭제
  private String clientId; // 클라이언트 id
  private String clientSecret; // 클라이언트 secret
  private String redirectUri; // Callback URL
  private int clientIpCnt; // 클라이언트 IP 목록 수
  private List<FinanceOrganizationServiceIp> clientIpList; // 클라이언트 IP 목록
}
