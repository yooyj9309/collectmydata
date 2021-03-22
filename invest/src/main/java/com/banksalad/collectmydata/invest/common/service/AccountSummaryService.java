package com.banksalad.collectmydata.invest.common.service;

import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountSummaryService {

  List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, String accountNum, long basicSearchTimestamp);

  void updateBasicResponseCode(long banksaladUserId, String organizationId, String accountNum, String responseCode);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, String accountNum, LocalDateTime transactionSyncedAt);

  void updateTransactionResponseCode(long banksaladUserId, String organizationId, String accountNum, String responseCode);

  void updateProductSearchTimestamp(long banksaladUserId, String organizationId, String accountNum, long productSearchTimestamp);

  void updateProductResponseCode(long banksaladUserId, String organizationId, String accountNum, String productResponseCode);
}
