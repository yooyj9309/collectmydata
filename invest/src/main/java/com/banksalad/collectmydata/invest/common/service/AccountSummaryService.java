package com.banksalad.collectmydata.invest.common.service;

import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;

import java.util.List;

public interface AccountSummaryService {

  List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, String accountNum, long basicSearchTimestamp);

  void updateBasicResponseCode(long banksaladUserId, String organizationId, String accountNum, String responseCode);
}
