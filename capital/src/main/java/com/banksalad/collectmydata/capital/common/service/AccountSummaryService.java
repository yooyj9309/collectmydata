package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountSummaryService {

  List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId,
      boolean demandOperatingLeaseAccount);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long basicSearchTimestamp);

  void updateBasicResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long detailSearchTimestamp);

  void updateDetailResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      LocalDateTime syncedAt);

  void updateTransactionResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updateOperatingLeaseBasicSearchTimestamp(long banksaladUserId, String organizationId,
      AccountSummary accountSummary, long operatingLeaseBasicSearchTimestamp);

  void updateOperatingLeaseBasicResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updateOperatingLeaseTransactionSyncedAt(long banksaladUserId, String organizationId,
      AccountSummary accountSummary, LocalDateTime syncStartedAt);

  void updateOperatingLeaseTransactionResponseCode(long banksaladUserId, String organizationId,
      AccountSummary accountSummary, String responseCode);
}
