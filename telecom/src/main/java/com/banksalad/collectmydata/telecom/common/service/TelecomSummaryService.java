package com.banksalad.collectmydata.telecom.common.service;

import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface TelecomSummaryService {

  List<TelecomSummary> listSummariesConsented(long banksaladUserId, String organizationId);

  LocalDateTime getTransactionSyncedAt(long banksaladUserId, String organizationId, TelecomSummary telecomSummary);

  LocalDateTime getPaidTransactionSyncedAt(long banksaladUserId, String organizationId, TelecomSummary telecomSummary);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, TelecomSummary telecomSummary,
      LocalDateTime transactionSyncedAt);

  void updatePaidTransactionSyncedAt(long banksaladUserId, String organizationId, TelecomSummary telecomSummary,
      LocalDateTime paidTransactionSyncedAt);

  void updateTransactionResponseCode(long banksaladUserId, String organizationId, TelecomSummary telecomSummary,
      String responseCode);

  void updatePaidTransactionResponseCode(long banksaladUserId, String organizationId, TelecomSummary telecomSummary,
      String responseCode);
}
