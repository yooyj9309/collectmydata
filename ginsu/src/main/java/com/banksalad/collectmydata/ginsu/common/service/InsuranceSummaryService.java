package com.banksalad.collectmydata.ginsu.common.service;

import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface InsuranceSummaryService {

  List<InsuranceSummary> listSummariesConsented(long banksaladUserId, String organizationId);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      long basicSearchTimestamp);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      LocalDateTime transactionSyncedAt);

  void updateBasicResponseCode(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      String responseCode);

  void updateTransactionResponseCode(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      String responseCode);
}
