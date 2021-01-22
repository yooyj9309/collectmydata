package com.banksalad.collectmydata.oauth.service;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.oauth.common.db.UserEntity;
import com.banksalad.collectmydata.oauth.common.repository.UserRedisRepository;
import com.banksalad.collectmydata.oauth.dto.IssueTokenRequest;
import com.banksalad.collectmydata.oauth.dto.OauthPageRequest;
import com.banksalad.collectmydata.oauth.dto.Organization;

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
  public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");


  public OauthServiceImpl(OrganizationService organizationService, UserRedisRepository userRedisRepository,
      OauthInfoService oauthInfoService) {
    this.organizationService = organizationService;
    this.userRedisRepository = userRedisRepository;
    this.oauthInfoService = oauthInfoService;
  }

  @Override
  public String ready(OauthPageRequest oauthPageRequest, Model model, Map<String, String> headers)
      throws Exception {

    Organization organization = organizationService
        .getOrganizationByObjectId(oauthPageRequest.getOrganizationObjectId());
    String state = generateStateAndKeepUserInfo(oauthPageRequest.getUserId(), organization);

    String redirectUrl = oauthInfoService.getRedirectUrl(organization.getMydataSector(), state, organization);
    model.addAttribute("redirectUrl", redirectUrl);
    return "pages/redirect";
  }

  @Override
  public String approve(IssueTokenRequest issueTokenRequest) {
    UserEntity userEntity = getUserInfo(issueTokenRequest.getState());

    // Token 발급 진행.
    // 해당 코드 안에서 Exception 코드작업 진행.
    //organizationService.issueToken(UserEntity);

    return "pages/oauth";
  }

  public UserEntity getUserInfo(String state) {
    return userRedisRepository.getUserInfo(state).orElseThrow(() -> {
      // TODO
      return new CollectRuntimeException("해당부분은 공통부분에서 메시지 정리후 변경 예정 ");
    });
  }

  public String generateStateAndKeepUserInfo(Long userId, Organization organization) {
    String state = UUID.randomUUID().toString();
    UserEntity userEntity = UserEntity.builder()
        .banksaladUserId(userId)
        .organizationCode(organization.getOrganizationCode())
        .organizationId(organization.getOrganizationId())
        .createdAt(LocalDateTime.now(UTC_ZONE_ID))
        .build();

    if (!userRedisRepository.setUserInfo(state, userEntity)) {
      //TODO
      throw new CollectRuntimeException("해당부분은 공통부분에서 메시지 정리후 변경 예정 ");
    }

    return state;
  }
}
