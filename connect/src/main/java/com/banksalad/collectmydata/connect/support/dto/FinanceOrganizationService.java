package com.banksalad.collectmydata.connect.support.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FinanceOrganizationService {

  private String serviceName; // 서비스명
  private String opType; // 서비스 정보의 구분 신규/수정/삭제
  private String clientId; // 클라이언트 id
  private String clientSecret; // 클라이언트 secret
  private String redirectUri; // Callback URL
  private Integer clientIpCnt; // 클라이언트 IP 목록 수
  private List<FinanceOrganizationServiceIp> clientIpList; // 클라이언트 IP 목록
}
