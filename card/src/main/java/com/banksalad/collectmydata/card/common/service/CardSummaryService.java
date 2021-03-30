package com.banksalad.collectmydata.card.common.service;


import com.banksalad.collectmydata.card.summary.dto.CardSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface CardSummaryService {

  List<CardSummary> listSummariesConsented(long banksaladUserId, String organizationId);

  void updateSearchTimestamp(long banksaladUserId, String organizationId, CardSummary cardSummary,
      long searchTimestamp);

  void updateResponseCode(long banksaladUserId, String organizationId, CardSummary cardSummary, String responseCode);

  void updateApprovalDomesticTransactionSyncedAt(long banksaladUserId, String organizationId, CardSummary cardSummary,
      LocalDateTime syncStartedAt);

  void updateApprovalDomesticTransactionResponseCode(long banksaladUserId, String organizationId,
      CardSummary cardSummary, String responseCode);
}
