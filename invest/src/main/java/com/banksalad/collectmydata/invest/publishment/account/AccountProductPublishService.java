package com.banksalad.collectmydata.invest.publishment.account;

import com.banksalad.collectmydata.invest.publishment.account.dto.AccountProductResponse;

import java.util.List;

public interface AccountProductPublishService {

  List<AccountProductResponse> getAccountProductResponses(long banksaladUserId, String organizationId);
}
