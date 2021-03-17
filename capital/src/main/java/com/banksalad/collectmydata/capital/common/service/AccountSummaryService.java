package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;

import java.util.List;

public interface AccountSummaryService {

  List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId,
      boolean isOperatingLeaseAccount);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long detailSearchTimestamp);

  void updateBasicResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);
}
