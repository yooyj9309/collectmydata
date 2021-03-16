package com.banksalad.collectmydata.finance.common.service;

import com.banksalad.collectmydata.finance.common.dto.OauthToken;

public interface OauthTokenService {

  OauthToken getOauthToken(long banksaladUserId, String OrganizationId);
}
