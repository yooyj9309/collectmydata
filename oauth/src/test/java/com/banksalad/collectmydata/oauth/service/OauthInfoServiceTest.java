package com.banksalad.collectmydata.oauth.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;
import com.banksalad.collectmydata.oauth.dto.Organization;
import com.banksalad.collectmydata.oauth.util.OauthTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("OauthInfoService Test")
public class OauthInfoServiceTest {

  @Autowired
  private OauthInfoService oauthInfoService;

  private final String state = "state_key";

  @Test
  @DisplayName("redirect 생성 테스트 - 정상적으로 redirectUrl 생성 되는 플로우")
  void getRedirectUrl_success() throws CollectException {
    Organization organization = OauthTestUtil.generateOrganization(MydataSector.FINANCE);
    String redirectUrl = oauthInfoService.getRedirectUrl(MydataSector.FINANCE, "state_key", organization);
    assertEquals(
        "https://testdomain.com/testapi?org_code=000&response_type=code&client_id=tmp&redirect_uri=/v1/mydata-auth/authorize&state=state_key",
        redirectUrl);
  }

  @Test
  @DisplayName("redirect 생성 테스트 - 지원하지않는 sector조회시 예외처리")
  void getRedirectUrl_exception() {
    Organization organization = OauthTestUtil.generateOrganization(MydataSector.HEALTHCARE);

    OauthException exception1 = getCollectException(MydataSector.HEALTHCARE, organization);
    assertEquals(exception1.getMessage(), OauthErrorType.INVALID_SECTOR.getErrorMsg());

    OauthException exception2 = getCollectException(MydataSector.PUBLIC, organization);
    assertEquals(exception2.getMessage(), OauthErrorType.INVALID_SECTOR.getErrorMsg());
  }

  private OauthException getCollectException(MydataSector mydataSector, Organization organization) {
    return assertThrows(
        OauthException.class,
        () -> oauthInfoService.getRedirectUrl(MydataSector.HEALTHCARE, state, organization)
    );
  }
}
