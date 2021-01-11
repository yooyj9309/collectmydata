package com.banksalad.collectmydata.bank.account.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Account {

  private Long accountId;
  private Long banksaladUserId;
  private String organizationId;
  private String accountNumber; // 계좌번호
  private String accountNumberRegistrationId; // uniqueness 2nd field
  private String maskedAccountNumber; // 마스킹 계좌번호
}
