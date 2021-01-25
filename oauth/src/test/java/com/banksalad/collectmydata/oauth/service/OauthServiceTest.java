package com.banksalad.collectmydata.oauth.service;

import com.banksalad.collectmydata.oauth.common.config.TestRedisConfiguration;
import com.banksalad.collectmydata.oauth.common.enums.MydataSector;
import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;
import com.banksalad.collectmydata.oauth.dto.OauthPageRequest;
import com.banksalad.collectmydata.oauth.grpc.client.AuthClient;
import com.banksalad.collectmydata.oauth.util.OauthTestUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
@DisplayName("OauthService Test")
public class OauthServiceTest {

  @Autowired
  private OauthService oauthService;

  @MockBean
  private AuthClient authClient;

  @MockBean
  private OrganizationService organizationService;

  private Map<String, String> headers = new HashMap<>();
  private final OauthPageRequest oauthPageRequest = OauthTestUtil.generateOauthPageRequest();

  @Test
  @DisplayName("OauthService ready 테스트 : 정상적인 플로우로 view name을 리턴하는 경우.")
  public void readyTest_success() throws Exception {
    mockSetting(MydataSector.FINANCE);
    Model model = new ConcurrentModel();

    // service call
    String returnString = oauthService.ready(oauthPageRequest, model, headers);

    // validate
    assertEquals(returnString, "pages/redirect");
    assertTrue(model.containsAttribute("redirectUrl"));
  }

  @Test
  @DisplayName("OauthService ready 테스트 : 올바르지 않은 sector로 인하여 exception을 던지는 경우")
  public void readyTest_invalidSector() throws Exception {
    mockSetting(MydataSector.HEALTHCARE);
    Model model = new ConcurrentModel();

    // service call
    Exception responseException = assertThrows(
        Exception.class,
        () -> oauthService.ready(oauthPageRequest, model, headers)
    );

    // validate
    assertThat(responseException).isInstanceOf(OauthException.class);
    assertEquals(OauthErrorType.INVALID_SECTOR.getErrorMsg(), responseException.getMessage());
  }

//  @Test
//  public void approve_success() {
//    //TODO connect 이후 작성
//  }

  private void mockSetting(MydataSector mydataSector) {
    when(authClient.getUserAuthInfoByToken(headers)).thenReturn(OauthTestUtil.generateUserAuthInfo());
    when(organizationService.getOrganizationByObjectId(OauthTestUtil.organizationObjectId))
        .thenReturn(OauthTestUtil.generateOrganization(mydataSector));
  }
}
