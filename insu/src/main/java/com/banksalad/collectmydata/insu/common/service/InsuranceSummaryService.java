package com.banksalad.collectmydata.insu.common.service;

import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface InsuranceSummaryService {

  List<InsuranceSummary> listSummariesConsented(long banksaladUserId, String organizationId);
  
  void updateBasicSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String insuNum,
      long basicSearchTimestamp, String rspCode);

  void updatePaymentSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String insuNum,
      long basicSearchTimestamp, String rspCode);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, String insuNum,
      LocalDateTime syncedAt);

  void updateCarSearchTimestamp(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      long searchTimestamp);

  void updateCarResponseCode(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      String responseCode);
}
