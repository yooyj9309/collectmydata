package com.banksalad.collectmydata.bank.account.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ListAccountsResponse {

  private final String resultCode;
  private final String resultMessage;

  private final List<Account> accounts;
}
