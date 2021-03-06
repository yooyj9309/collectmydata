package com.banksalad.collectmydata.oauth.controller;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.banksalad.collectmydata.oauth.dto.IssueTokenRequest;
import com.banksalad.collectmydata.oauth.dto.OauthPageRequest;
import com.banksalad.collectmydata.oauth.service.OauthService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/mydata-auth")
public class OauthController {

  private final OauthService oauthService;

  @GetMapping("/ready")
  public String getOauthPage(ServerHttpRequest request, @Valid OauthPageRequest oauthPageRequest, Model model) {
    return oauthService.ready(request, oauthPageRequest, model);
  }

  @GetMapping("/authorize")
  public String issueToken(@Valid IssueTokenRequest issueTokenRequest) {
    return oauthService.approve(issueTokenRequest);
  }
}
