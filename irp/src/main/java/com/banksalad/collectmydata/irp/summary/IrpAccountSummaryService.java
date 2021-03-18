package com.banksalad.collectmydata.irp.summary;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface IrpAccountSummaryService {

  void saveAccountSummaries(ExecutionContext executionContext) throws ResponseNotOkException;

  List<IrpAccountSummary> listConsentedAccountSummaries(long banksaladUserId, String organizationId);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      long basicSearchTimestamp);

  void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      long detailSearchTimestamp);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      LocalDateTime transactionSyncedAt);

  void updateBasicResponseCode(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      String responseCode);

  void updateDetailResponseCode(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      String responseCode);

  void updateTransactionResponseCode(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      String responseCode);
}
