package com.banksalad.collectmydata.oauth.service;


import com.banksalad.collectmydata.oauth.dto.IssueTokenRequest;
import com.banksalad.collectmydata.oauth.dto.OauthPageRequest;

import org.springframework.ui.Model;

import java.util.Map;

public interface OauthService {

  public String ready(OauthPageRequest oauthPageRequest, Model model, Map<String, String> headers);

  public String approve(IssueTokenRequest issueTokenRequest); // 작업하면서 return값 변경 예정
}
