package com.banksalad.collectmydata.invest.publishment.account;

import com.banksalad.collectmydata.invest.publishment.account.dto.AccountTransactionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountTransactionPublishService {

  List<AccountTransactionResponse> getAccountTransactionResponses(long banksaladUserId, String organizationId,
      String accountNum, LocalDateTime createdAfterMs, int limit);
}
