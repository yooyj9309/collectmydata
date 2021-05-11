package com.banksalad.collectmydata.oauth.service;


import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.ui.Model;

import com.banksalad.collectmydata.oauth.dto.IssueTokenRequest;
import com.banksalad.collectmydata.oauth.dto.OauthPageRequest;

public interface OauthService {

  String ready(ServerHttpRequest request, OauthPageRequest oauthPageRequest, Model model);

  String approve(IssueTokenRequest issueTokenRequest); // 작업하면서 return값 변경 예정
}
