package com.banksalad.collectmydata.connect.common.meters;

import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.enums.TokenErrorType;

public interface ConnectMeterRegistry {

  // 데이터 보유 기관에 토큰 요청시 발생하는 에러(접근토큰 발급/갱신/폐기)
  void incrementTokenErrorCount(String organizationId, TokenErrorType tokenErrorType);

  // 서비스 로직에서 발생하는 에러
  void incrementServiceErrorCount(String organizationId, ConnectErrorType connectErrorType);
}
