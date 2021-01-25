package com.banksalad.collectmydata.oauth.service;

import com.banksalad.collectmydata.oauth.common.config.TestRedisConfiguration;
import com.banksalad.collectmydata.oauth.common.db.UserEntity;
import com.banksalad.collectmydata.oauth.common.enums.MydataSector;
import com.banksalad.collectmydata.oauth.dto.Organization;
import com.banksalad.collectmydata.oauth.dto.UserAuthInfo;
import com.banksalad.collectmydata.oauth.util.OauthTestUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
@DisplayName("OauthInfoServiceImpl Test")
public class OauthServiceImplTest {

  @Autowired
  private OauthServiceImpl oauthServiceImpl;

  private final int userId = 1;
  private final String organizationCode = "000";
  private final String os = "android";
  private final String organizationId = "shinhancard";

  @Test
  @DisplayName("OauthServiceImpl 를 통해 UserEntity를 저장 및 조회하는 테스트")
  public void getAndSetUserInfoTest() {
    UserAuthInfo userAuthInfo = OauthTestUtil.generateUserAuthInfo();
    Organization organization = OauthTestUtil.generateOrganization(MydataSector.FINANCE);
    String state = oauthServiceImpl.generateStateAndKeepUserInfo(userAuthInfo, organization);

    UserEntity responseEntity = oauthServiceImpl.getUserInfo(state);
    assertThat(responseEntity).usingRecursiveComparison()
        .ignoringFields("createdAt")
        .isEqualTo(UserEntity.builder()
            .banksaladUserId((long) userId)
            .organizationId(organizationId)
            .organizationCode(organizationCode)
            .os(os)
            .build()
        );
  }
}
