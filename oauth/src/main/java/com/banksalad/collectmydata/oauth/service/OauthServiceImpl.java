package com.banksalad.collectmydata.oauth.service;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.banksalad.collectmydata.oauth.common.db.UserEntity;
import com.banksalad.collectmydata.oauth.common.enums.AuthorizationResultType;
import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;
import com.banksalad.collectmydata.oauth.common.exception.AuthorizationException;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;
import com.banksalad.collectmydata.oauth.common.meters.OauthMeterRegistry;
import com.banksalad.collectmydata.oauth.common.meters.OauthMeterRegistryImpl;
import com.banksalad.collectmydata.oauth.common.repository.UserRedisRepository;
import com.banksalad.collectmydata.oauth.dto.IssueTokenRequest;
import com.banksalad.collectmydata.oauth.dto.OauthPageRequest;
import com.banksalad.collectmydata.oauth.dto.Organization;
import com.banksalad.collectmydata.oauth.dto.UserAuthInfo;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthServiceImpl implements OauthService {

  private final OrganizationService organizationService;
  private final UserRedisRepository userRedisRepository;
  private final OauthInfoService oauthInfoService;
  private final OauthMeterRegistry oauthMeterRegistry;
  private final AuthService authService;
  public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

  @Override
  public String ready(ServerHttpRequest httpRequest, OauthPageRequest oauthPageRequest, Model model) {
    // banksalad token을 통해 user정보조회, organizationObjectId를 통해 organization조회
    Organization organization = organizationService
        .getOrganizationByObjectId(oauthPageRequest.getOrganizationObjectId());
    UserAuthInfo userAuthInfo = authService.getUserAuthInfo(organization.getOrganizationId(), httpRequest);

    // 유저정보 저장및 key return
    String state = generateStateAndKeepUserInfo(userAuthInfo, organization);

    // redirect url 생성
    String redirectUrl = oauthInfoService.getRedirectUrl(organization.getMydataSector(), state, organization);

    // metric logging
    oauthMeterRegistry.incrementUserAuthStepCount(organization.getOrganizationId(), userAuthInfo.getOs(),
        OauthMeterRegistryImpl.OAUTH_INIT);
    model.addAttribute("redirectUrl", redirectUrl);
    return "pages/redirect";
  }

  @Override
  public String approve(IssueTokenRequest issueTokenRequest) {
    // state를 통해 유저 정보 조회.
    UserEntity userEntity = getUserInfo(issueTokenRequest.getState());

    // error param 검증
    if (!StringUtil.isNullOrEmpty(issueTokenRequest.getError())) {
      validateError(issueTokenRequest.getError(), userEntity.getOrganizationId());
    }

    // 토큰발급
    organizationService.issueToken(userEntity, issueTokenRequest.getCode());

    // metric logging
    oauthMeterRegistry.incrementUserAuthStepCount(userEntity.getOrganizationId(), userEntity.getOs(),
        OauthMeterRegistryImpl.OAUTH_COMPLETE);
    return "pages/oauth";
  }

  public UserEntity getUserInfo(String state) {
    return userRedisRepository.getUserInfo(state).orElseThrow(() -> {
      throw new OauthException(OauthErrorType.USER_NOT_FOUND, "UNKNOWN");
    });
  }

  public String generateStateAndKeepUserInfo(UserAuthInfo userAuthInfo, Organization organization) {
    String state = UUID.randomUUID().toString();
    UserEntity userEntity = UserEntity.builder()
        .banksaladUserId(Long.valueOf(userAuthInfo.getBanksaladUserId()))
        .organizationCode(organization.getOrganizationCode())
        .organizationId(organization.getOrganizationId())
        .os(userAuthInfo.getOs())
        .createdAt(LocalDateTime.now(UTC_ZONE_ID))
        .build();

    if (!userRedisRepository.setUserInfo(state, userEntity)) {
      throw new OauthException(OauthErrorType.FAILED_TO_SAVE_USER, organization.getOrganizationId());
    }
    return state;
  }

  public void validateError(String error, String organizationId) {
    AuthorizationResultType errorType = AuthorizationResultType.getAuthorizationResultCode(error);
    if (errorType != AuthorizationResultType.SUCCESS) {
      throw new AuthorizationException(errorType, organizationId);
    }
  }
}
