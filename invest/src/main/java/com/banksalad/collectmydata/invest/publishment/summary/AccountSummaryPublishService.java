package com.banksalad.collectmydata.invest.publishment.summary;

import com.banksalad.collectmydata.invest.publishment.summary.dto.AccountSummaryResponse;

import java.util.List;

public interface AccountSummaryPublishService {

  List<AccountSummaryResponse> getAccountSummaryResponses(long banksaladUserId, String organizationId);
}
