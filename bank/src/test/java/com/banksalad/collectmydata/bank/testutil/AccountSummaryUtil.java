package com.banksalad.collectmydata.bank.testutil;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;

import java.time.LocalDateTime;

public final class AccountSummaryUtil {

  private AccountSummaryUtil() {

  }

  public static AccountSummaryEntity createDepositAccountSummary(String organizationId, Long banksaladUserId,
      String accountNum, String seqNo, LocalDateTime syncedAt) {

    // TODO jayden-lee 매번 생성하는 것이 아닌 미리 Mock 객체를 여러개 생성 해놓고 반환 하도록 수정

    return AccountSummaryEntity.builder()
        .organizationId(organizationId)
        .banksaladUserId(banksaladUserId)
        .accountNum(accountNum)
        .seqno(seqNo)
        .consent(true)
        .foreignDeposit(false)
        .prodName("자유입출금")
        .accountType("1001")
        .accountStatus("01")
        .syncedAt(syncedAt)
        .build();
  }
}
