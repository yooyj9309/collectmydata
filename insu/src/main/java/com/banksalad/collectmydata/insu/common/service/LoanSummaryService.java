package com.banksalad.collectmydata.insu.common.service;

import java.time.LocalDateTime;

public interface LoanSummaryService {
  
  void updateBasicSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp, String rspCode);

  void updateDetailSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp, String rspCode);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, String accountNum,
      LocalDateTime transactionSyncedAt);

}
