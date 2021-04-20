package com.banksalad.collectmydata.oauth.service;

import org.springframework.http.server.reactive.ServerHttpRequest;

import com.banksalad.collectmydata.oauth.dto.UserAuthInfo;

public interface AuthService {

  public UserAuthInfo getUserAuthInfo(String organizationId, ServerHttpRequest httpRequest);
}
