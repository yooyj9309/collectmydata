package com.banksalad.collectmydata.insu.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface InsuranceSummaryService {

  List<InsuranceSummary> listInsuranceSummaries(ExecutionContext executionContext, String organizationCode);

  void updateBasicSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String insuNum,
      long basicSearchTimestamp, String rspCode);

  void updateCarSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String insuNum,
      long basicSearchTimestamp, String rspCode);

  void updatePaymentSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String insuNum,
      long basicSearchTimestamp, String rspCode);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, String insuNum,
      LocalDateTime syncedAt);

}
