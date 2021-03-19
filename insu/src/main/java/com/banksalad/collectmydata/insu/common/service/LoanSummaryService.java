package com.banksalad.collectmydata.insu.common.service;

import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanSummaryService {

  List<LoanSummary> listLoanSummaries(long banksaladUserId, String organizationId);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp);

  void updateBasicResponseCode(long banksaladUserId, String organizationId, String accountNum,
      String rspCode);

  void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp);

  void updateDetailResponseCode(long banksaladUserId, String organizationId, String accountNum,
      String rspCode);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, String accountNum,
      LocalDateTime transactionSyncedAt);

  void updateTransactionResponseCode(long banksaladUserId, String organizationId, String accountNum,
      String rspCode);

}
