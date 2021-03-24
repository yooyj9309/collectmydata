package com.banksalad.collectmydata.ginsu.common.service;

import com.banksalad.collectmydata.ginsu.summary.dto.GinsuSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface GinsuSummaryService {

  List<GinsuSummary> listSummariesConsented(long banksaladUserId, String organizationId);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, GinsuSummary ginsuSummary,
      long basicSearchTimestamp);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, GinsuSummary ginsuSummary,
      LocalDateTime transactionSyncedAt);

  void updateBasicResponseCode(long banksaladUserId, String organizationId, GinsuSummary ginsuSummary,
      String responseCode);

  void updateTransactionResponseCode(long banksaladUserId, String organizationId, GinsuSummary ginsuSummary,
      String responseCode);
}
