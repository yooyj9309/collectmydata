package com.banksalad.collectmydata.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.oauth.common.config.TestRedisConfiguration;
import com.banksalad.collectmydata.oauth.common.enums.AuthorizationResultType;
import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;
import com.banksalad.collectmydata.oauth.common.exception.AuthorizationException;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;
import com.banksalad.collectmydata.oauth.common.repository.UserRedisRepository;
import com.banksalad.collectmydata.oauth.dto.IssueTokenRequest;
import com.banksalad.collectmydata.oauth.dto.OauthPageRequest;
import com.banksalad.collectmydata.oauth.util.OauthTestUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectGrpc.CollectmydataconnectBlockingStub;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.IssueTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestRedisConfiguration.class})
@DisplayName("OauthService Test")
public class OauthServiceTest {

  @Autowired
  private OauthService oauthService;

  @MockBean
  private AuthService authService;

  @MockBean
  private UserRedisRepository userRedisRepository;

  @MockBean
  private CollectmydataconnectBlockingStub collectmydataconnectBlockingStub;

  private Map<String, String> headers = new HashMap<>();

  MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.get("http://localhost/get")
      .header("Authorization", "token")
      .header("banksalad-application-name", "and&ios");

  ServerHttpRequest request = adaptFromForwardedHeaders(builder);
  //  );
  private final OauthPageRequest oauthPageRequest = OauthTestUtil.generateOauthPageRequest();

  @Test
  @DisplayName("OauthService ready 테스트 : 정상적인 플로우로 view name을 리턴하는 경우.")
  public void readyTest_success() throws Exception {
    ready_mockSetting(MydataSector.FINANCE);
    Model model = new ConcurrentModel();

    // service call
    String returnString = oauthService.ready(request, oauthPageRequest, model);

    // validate
    assertEquals(returnString, "pages/redirect");
    assertTrue(model.containsAttribute("redirectUrl"));
  }

//  cache 로 인하여 테스트가 깨지는것으로 보입니다. 현재 확인중입니다.
//  @Test
//  @DisplayName("OauthService ready 테스트 : 올바르지 않은 sector로 인하여 exception을 던지는 경우")
//  public void readyTest_invalidSector() throws Exception {
//    ready_mockSetting(MydataSector.HEALTHCARE);
//    Model model = new ConcurrentModel();
//
//    // service call
//    Exception responseException = assertThrows(
//        Exception.class,
//        () -> oauthService.ready(request, oauthPageRequest, model)
//    );
//
//    // validate
//    assertThat(responseException).isInstanceOf(OauthException.class);
//    assertEquals(OauthErrorType.INVALID_SECTOR.getErrorMsg(), responseException.getMessage());
//  }

  @Test
  @DisplayName("OauthService approve 테스트 : 정상적으로 인가코드 및 error가 전달되어 토큰을 발급하는 경우.")
  public void approve_success() {
    IssueTokenRequest issueTokenRequest = issueTokenRequestAssembler("success");
    approve_mockSetting(false);

    String returnString = oauthService.approve(issueTokenRequest);
    assertEquals("pages/oauth", returnString);
  }

  @Test
  @DisplayName("OauthService approve 테스트 : connectmydataBlockingStub에서 에러가 오는경우.")
  public void approve_connectmydataClientException() throws Exception {
    IssueTokenRequest issueTokenRequest = issueTokenRequestAssembler("success");
    approve_mockSetting(true);

    Exception responseException = assertThrows(
        Exception.class,
        () -> oauthService.approve(issueTokenRequest)
    );
    assertThat(responseException).isInstanceOf(OauthException.class);
    assertEquals(OauthErrorType.FAILED_CONNECT_ISSUETOKEN_RPC.getErrorMsg(), responseException.getMessage());
  }

  @Test
  @DisplayName("OauthService approve 테스트 : 예외 상황의 error parameter가 오는경우.")
  public void approve_errorParam1() {
    approve_mockSetting(false);

    IssueTokenRequest issueTokenRequest2 = issueTokenRequestAssembler("invalid_scope");
    Exception responseException = assertThrows(
        Exception.class,
        () -> oauthService.approve(issueTokenRequest2)
    );
    assertThat(responseException).isInstanceOf(AuthorizationException.class);
    assertEquals(AuthorizationResultType.INVALID_SCOPE.getError(), responseException.getMessage());
  }

  @Test
  @DisplayName("OauthService approve 테스트 : 예외 상황의 error parameter가 오는경우.")
  public void approve_errorParam2() {
    approve_mockSetting(false);

    IssueTokenRequest issueTokenRequest1 = issueTokenRequestAssembler("ERROROROR");
    Exception responseException = assertThrows(
        Exception.class,
        () -> oauthService.approve(issueTokenRequest1)
    );
    assertThat(responseException).isInstanceOf(AuthorizationException.class);
    assertEquals(AuthorizationResultType.UNKNOWN.getError(), responseException.getMessage());
  }

  private IssueTokenRequest issueTokenRequestAssembler(String error) {
    IssueTokenRequest issueTokenRequest = new IssueTokenRequest();
    issueTokenRequest.setCode(OauthTestUtil.organizationCode);
    issueTokenRequest.setError(error);
    issueTokenRequest.setState(OauthTestUtil.state);

    return issueTokenRequest;
  }

  private void ready_mockSetting(MydataSector mydataSector) {
    when(authService.getUserAuthInfo(OauthTestUtil.organizationId, request))
        .thenReturn(OauthTestUtil.generateUserAuthInfo());
//    when(organizationService.getOrganizationByObjectId(any())).thenReturn(
//        OauthTestUtil.generateOrganization(mydataSector));
    when(collectmydataconnectBlockingStub.getOrganizationByOrganizationGuid(any()))
        .thenReturn(OauthTestUtil.getOrganizationResponseAssembler(mydataSector));
    when(userRedisRepository.setUserInfo(any(), any())).thenReturn(true);
  }

  private void approve_mockSetting(Boolean hasException) {
    when(userRedisRepository.getUserInfo(OauthTestUtil.state))
        .thenReturn(Optional.of(OauthTestUtil.userEntityAssembler()));

    if (hasException) {
      when(collectmydataconnectBlockingStub.issueToken(any())).thenThrow(new RuntimeException());
    } else {
      when(collectmydataconnectBlockingStub.issueToken(any())).thenReturn(
          IssueTokenResponse.getDefaultInstance());
    }
  }

  private ServerHttpRequest adaptFromForwardedHeaders(MockServerHttpRequest.BaseBuilder<?> builder) {
    AtomicReference<ServerHttpRequest> requestRef = new AtomicReference<>();
    MockServerWebExchange exchange = MockServerWebExchange.from(builder);
    new org.springframework.web.filter.reactive.ForwardedHeaderFilter().filter(exchange, exchange2 -> {
      requestRef.set(exchange2.getRequest());
      return Mono.empty();
    }).block();
    return requestRef.get();
  }
}
