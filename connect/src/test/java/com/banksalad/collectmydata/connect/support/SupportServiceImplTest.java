package com.banksalad.collectmydata.connect.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationClientEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationOauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationClientRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationOauthTokenRepository;
import com.banksalad.collectmydata.connect.support.service.SupportServiceImpl;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.banksalad.collectmydata.connect.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("SupportServiceImpl Test")
public class SupportServiceImplTest {

  @Autowired
  private SupportServiceImpl supportServiceImpl;

  @Autowired
  private OrganizationClientRepository organizationClientRepository;

  @Autowired
  private OrganizationOauthTokenRepository organizationOauthTokenRepository;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().port(9091));

  @BeforeEach
  public void setupClass() {
    wiremock.start();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("엑세스 토큰 발급 테스트")
  void getAccessTokenTest() {
    setupServer();
    ExecutionContext executionContext = ExecutionContext.builder()
        .accessToken("test")
        .organizationHost("http://localhost:9091")
        .build();

    organizationClientRepository.save(
        OrganizationClientEntity.builder()
            .clientId("clientId")
            .clientSecret("clientSecret")
            .organizationId("banksalad")
            .build()

    );

    String token = supportServiceImpl.getAccessToken(executionContext);
    assertEquals("accessToken", token);

    assertThat(organizationOauthTokenRepository.findByOrganizationId("banksalad").orElse(null))
        .usingRecursiveComparison()
        .ignoringFields("accessTokenExpiresAt", "organizationOauthTokenId")
        .isEqualTo(OrganizationOauthTokenEntity.builder()
            .organizationId("banksalad")
            .accessToken("accessToken")
            .accessTokenExpiresIn(123456789)
            .tokenType("Bearer")
            .scope("manage")
            .build()
        );

  }

  private void setupServer() {
    // 계좌목록조회 page 01
    wiremock.stubFor(post(urlMatching("/oauth/2.0/token"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/api7/SU01_001.json"))));
  }
}
