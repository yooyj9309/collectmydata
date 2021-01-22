package com.banksalad.collectmydata.oauth.service;

import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.oauth.common.enums.MydataSector;
import com.banksalad.collectmydata.oauth.dto.Organization;

public interface OauthInfoService {

  public String getRedirectUrl(MydataSector sector, String key, Organization organization) throws CollectException;

}
