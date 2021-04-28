package com.banksalad.collectmydata.bank.publishment.summary;

import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummaryResponse;

import java.util.List;

public interface AccountSummaryPublishService {

  List<AccountSummaryResponse> getAccountSummaryResponses(long banksaladUserId, String organizationId);
}
