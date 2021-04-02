package com.banksalad.collectmydata.efin.common.service;

import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountSummaryService {

  List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId);

  void updateBalanceResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updateBalanceSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long searchTimestamp);

  void updateChargeResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updateChargeSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long searchTimestamp);

  void updatePrepaidTransactionResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updatePrepaidTransactionSyncedAt(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      LocalDateTime syncStartedAt);

  void updateTransactionResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      LocalDateTime syncStartedAt);
}
