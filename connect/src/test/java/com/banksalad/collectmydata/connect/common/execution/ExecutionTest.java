package com.banksalad.collectmydata.connect.common.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.connect.collect.Executions;
import com.banksalad.collectmydata.connect.token.dto.GetIssueTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.GetOauthTokenResponse;
import com.banksalad.collectmydata.connect.token.dto.GetRefreshTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.GetRevokeTokenRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.banksalad.collectmydata.connect.common.ConnectConstant.ACCESS_TOKEN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ACCESS_TOKEN_EXPIRES_IN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.AUTHORIZATION_CODE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.CLIENT_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.CLIENT_SECRET;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.REDIRECT_URI;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.REFRESH_TOKEN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.REFRESH_TOKEN_EXPIRES_IN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.SCOPE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.TOKEN_TYPE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.TOKEN_TYPE_HINT;
import static com.banksalad.collectmydata.connect.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ExecutionTest {

  @Autowired
  private CollectExecutor collectExecutor;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void tearDownAll() {
    wireMockServer.shutdown();
  }

  @Test
  @DisplayName("5.1.2 접근토큰 발급 요청")
  void requestAccessTokenTest() {
    // given
    ExecutionContext executionContext = getExecutionContext();

    GetIssueTokenRequest request = GetIssueTokenRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .code(AUTHORIZATION_CODE)
        .clientId(CLIENT_ID)
        .clientSecret(CLIENT_SECRET)
        .redirectUri(REDIRECT_URI)
        .build();
    ExecutionRequest<GetIssueTokenRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(request);

    // when
    ExecutionResponse<GetOauthTokenResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.oauth_issue_token, executionRequest);
    GetOauthTokenResponse response = executionResponse.getResponse();

    // then
    assertThat(response).usingRecursiveComparison().isEqualTo(
        GetOauthTokenResponse.builder()
            .tokenType(TOKEN_TYPE)
            .accessToken(ACCESS_TOKEN)
            .expiresIn(ACCESS_TOKEN_EXPIRES_IN)
            .refreshToken(REFRESH_TOKEN)
            .refreshTokenExpiresIn(REFRESH_TOKEN_EXPIRES_IN)
            .scope(SCOPE)
            .build());
  }

  @Test
  @DisplayName("5.1.3 접근토큰 갱신")
  void requestRefreshTokenTest() {
    // given
    ExecutionContext executionContext = getExecutionContext();

    GetRefreshTokenRequest request = GetRefreshTokenRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .refreshToken(REFRESH_TOKEN)
        .clientId(CLIENT_ID)
        .clientSecret(CLIENT_SECRET)
        .build();
    ExecutionRequest<GetRefreshTokenRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(request);

    // when
    ExecutionResponse<GetOauthTokenResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.oauth_refresh_token, executionRequest);
    GetOauthTokenResponse response = executionResponse.getResponse();

    // then
    assertThat(response).usingRecursiveComparison().isEqualTo(
        GetOauthTokenResponse.builder()
            .tokenType(TOKEN_TYPE)
            .accessToken(ACCESS_TOKEN)
            .expiresIn(ACCESS_TOKEN_EXPIRES_IN)
            .refreshToken(REFRESH_TOKEN)
            .refreshTokenExpiresIn(REFRESH_TOKEN_EXPIRES_IN)
            .scope(SCOPE)
            .build());
  }

  @Test
  @DisplayName("5.1.4 접근토큰 폐기")
  void requestRevokeTokenTest() {
    // given
    ExecutionContext executionContext = getExecutionContext();

    GetRevokeTokenRequest request = GetRevokeTokenRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .token(ACCESS_TOKEN)
        .tokenTypeHint(TOKEN_TYPE_HINT)
        .clientId(CLIENT_ID)
        .clientSecret(CLIENT_SECRET)
        .build();
    ExecutionRequest<GetRevokeTokenRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(request);

    // when
    ExecutionResponse<GetOauthTokenResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.oauth_revoke_token, executionRequest);
    GetOauthTokenResponse response = executionResponse.getResponse();

    // then
    assertThat(response).usingRecursiveComparison().isEqualTo(
        GetOauthTokenResponse.builder()
        .build());
  }

  private ExecutionContext getExecutionContext() {
    return ExecutionContext.builder()
        .organizationId(ORGANIZATION_ID)
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + wireMockServer.port())
        .build();
  }

  private static void setupMockServer() {
    // TODO : request context type -> application/x-www-form-urlencoded

    // 5.1.2 접근토큰 발급 요청
    wireMockServer.stubFor(post(urlMatching("/oauth/2.0/token"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/AU02_001.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/AU02_001.json"))));

    // 5.1.3 접근토큰 갱신
    wireMockServer.stubFor(get(urlMatching("/oauth/2.0/token"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/AU02_002.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/AU02_002.json"))));

    // 5.1.4 접근토큰 폐기
    wireMockServer.stubFor(get(urlMatching("/oauth/2.0/revoke"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/AU03_001.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/AU03_001.json"))));
  }
}
