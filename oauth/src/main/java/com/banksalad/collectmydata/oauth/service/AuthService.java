package com.banksalad.collectmydata.oauth.service;

import com.banksalad.collectmydata.oauth.dto.UserAuthInfo;

import java.util.Map;

public interface AuthService {

  public UserAuthInfo getUserAuthInfo(String organizationId, Map<String, String> headers);
}
