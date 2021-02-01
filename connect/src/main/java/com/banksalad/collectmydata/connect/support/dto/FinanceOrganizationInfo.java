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
public class FinanceOrganizationInfo {

  // 7.1.2 사용목록
  private String opType; //기관정보의 구분 ( 신규/삭제/수정)
  private String orgCode; // 기관코드
  private String orgType; // 기관구분
  private String orgName; // 기관명
  private String orgRegno; //사업자 등록번호
  private String corpRegno; // 법인등록번호
  private String address; // 주소
  private String domain; // API 서버 도메인명(또는 IP)
  private String relayOrgCode; //중계기관 기관코드

  //7.1.3 사용 목록
  private Integer serviceCnt; // 서비스 목록
  private List<FinanceOrganizationService> serviceList; // 서비스 목록
}
