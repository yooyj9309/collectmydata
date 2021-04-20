package com.banksalad.collectmydata.bank.publishment.summary;

import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummaryResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankAccountSummariesRequest;

import java.util.List;

public interface AccountSummaryPublishService {

  List<AccountSummaryResponse> getAccountSummaryResponses(ListBankAccountSummariesRequest request);
}
