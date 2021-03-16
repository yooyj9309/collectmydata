package com.banksalad.collectmydata.bank.common.enums;

import java.util.List;

public enum BankAccountType {
  UNKNOWN,
  DEPOSIT,
  LOAN,
  INVEST;

  // TODO jayden-lee 마이너스 통장 계좌는 API 호출 유의해야 함 (기본정보: 대출, 추가정보: 수신, 거래내역: 수신)
  public static List<String> depositAccountTypeCodes = List.of("1001", "1002", "1003", "1999");
  public static List<String> investAccountTypeCodes = List.of("2001", "2002", "2003", "2999");
  public static List<String> loanAccountTypeCodes = List.of("3001", "3150", "3170", "3200", "3210", "3220",
      "3230", "3240", "3245", "3250", "3260", "3270", "3271", "3290", "3400", "3500", "3510", "3590", "3700", "3710",
      "3999");

  public static BankAccountType of(String code) {
    if (depositAccountTypeCodes.contains(code)) {
      return DEPOSIT;

    } else if (investAccountTypeCodes.contains(code)) {
      return INVEST;

    } else if (loanAccountTypeCodes.contains(code)) {
      return LOAN;

    } else {
      return UNKNOWN;
    }
  }
}
