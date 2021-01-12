package com.banksalad.collectmydata.oauth.controller;

import com.banksalad.collectmydata.oauth.service.OauthService;
import com.banksalad.collectmydata.oauth.service.OrganizationService;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/mydata-auth")
public class OauthController {

  private final OauthService oauthService;
  private final OrganizationService organizationService;
  
  @GetMapping("/ready")
  public String getOauthPage(ServerHttpRequest request, String organizationObjectId, Long userId, Model model) {
    // TODO
    // 1. 기본 파라미터 및 넘어온 기관코드를 검증한다.
    // 2. UUID를 통해 key를 생성, 유저 정보를 저장한다.
    // 3. 리다이렉트url을 만든다.
    // 로깅 및 예외처리 필
    // 리턴
    String organizationCode = organizationService.getOrganizationByObjectId(organizationObjectId);
    String state = oauthService.keepUserInfo(userId, organizationCode, request.getHeaders().toSingleValueMap());

    model.addAttribute("redirectUrl", oauthService.getRedirectUrl(state, organizationCode));
    return "pages/redirect";
  }

  @GetMapping("/authorize")
  public String issueToken(String state) {
    // (5.1.1 인가코드 발급 요청의 redirectUrl에 의하여 호출)
    // state 를 통해 유저 정보를 조회한다.
    // 해당 값을 통하여 connect에 토큰발급 요청을 보낸다.
    // 리턴
    return "pages/oauth";
  }
}
