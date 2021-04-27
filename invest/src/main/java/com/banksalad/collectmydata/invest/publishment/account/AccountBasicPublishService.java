package com.banksalad.collectmydata.invest.publishment.account;

import com.banksalad.collectmydata.invest.publishment.account.dto.AccountBasicResponse;

import java.util.List;

public interface AccountBasicPublishService {

  List<AccountBasicResponse> getAccountBasicResponses(long banksaladUserId, String organizationId);
}
