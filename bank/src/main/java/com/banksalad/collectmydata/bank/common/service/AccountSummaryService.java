package com.banksalad.collectmydata.bank.common.service;

import com.banksalad.collectmydata.bank.common.enums.BankAccountType;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountSummaryService {

  List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId,
      BankAccountType bankAccountType);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long basicSearchTimestamp);

  void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long detailSearchTimestamp);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      LocalDateTime transactionSyncedAt);

  void updateBasicResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updateDetailResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updateTransactionResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);
}
