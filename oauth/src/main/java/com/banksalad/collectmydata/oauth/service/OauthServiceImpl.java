package com.banksalad.collectmydata.oauth.service;

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
import com.banksalad.collectmydata.oauth.grpc.client.AuthClient;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class OauthServiceImpl implements OauthService {

  private final OrganizationService organizationService;
  private final UserRedisRepository userRedisRepository;
  private final OauthInfoService oauthInfoService;
  private final OauthMeterRegistry oauthMeterRegistry;
  private final AuthClient authClient;
  public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");


  public OauthServiceImpl(OrganizationService organizationService, UserRedisRepository userRedisRepository,
      OauthInfoService oauthInfoService, OauthMeterRegistry oauthMeterRegistry, AuthClient authClient) {
    this.organizationService = organizationService;
    this.userRedisRepository = userRedisRepository;
    this.oauthInfoService = oauthInfoService;
    this.oauthMeterRegistry = oauthMeterRegistry;
    this.authClient = authClient;
  }

  @Override
  public String ready(OauthPageRequest oauthPageRequest, Model model, Map<String, String> headers)
      throws Exception {
    UserAuthInfo userAuthInfo = authClient.getUserAuthInfoByToken(headers);
    Organization organization = organizationService
        .getOrganizationByObjectId(oauthPageRequest.getOrganizationObjectId());
    String state = generateStateAndKeepUserInfo(userAuthInfo, organization);

    String redirectUrl = oauthInfoService.getRedirectUrl(organization.getMydataSector(), state, organization);

    // metric logging
    oauthMeterRegistry.incrementUserAuthStepCount(organization.getOrganizationId(), userAuthInfo.getOs(),
        OauthMeterRegistryImpl.OAUTH_INIT);
    model.addAttribute("redirectUrl", redirectUrl);
    return "pages/redirect";
  }

  @Override
  public String approve(IssueTokenRequest issueTokenRequest) {
    UserEntity userEntity = getUserInfo(issueTokenRequest.getState());

    validateError(issueTokenRequest.getState(), userEntity.getOrganizationId());
    // Token 발급 진행.
    // 해당 코드 안에서 Exception 코드작업 진행.
    //organizationService.issueToken(UserEntity);

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
