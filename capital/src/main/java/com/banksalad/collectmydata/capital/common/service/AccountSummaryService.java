package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;

import java.util.List;

public interface AccountSummaryService {

  List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId,
      boolean demandOperatingLeaseAccount);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long detailSearchTimestamp);

  void updateBasicResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);

  void updateOperatingLeaseBasicSearchTimestamp(long banksaladUserId, String organizationId,
      AccountSummary accountSummary, long operatingLeaseBasicSearchTimestamp);

  void updateOperatingLeaseBasicResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode);
}
