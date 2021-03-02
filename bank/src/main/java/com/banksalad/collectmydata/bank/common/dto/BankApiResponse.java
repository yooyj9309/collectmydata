package com.banksalad.collectmydata.bank.common.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankApiResponse {

  // TODO jayden-lee 수신, 대출, 투자, IRP 계좌 정보와 거래내역 목록 프로퍼티 추가
}
