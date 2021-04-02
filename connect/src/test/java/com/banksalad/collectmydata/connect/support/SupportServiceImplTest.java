package com.banksalad.collectmydata.connect.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.connect.common.db.entity.BanksaladClientSecretEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationOauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.repository.BanksaladClientSecretRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationOauthTokenRepository;
import com.banksalad.collectmydata.connect.common.enums.SecretType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.support.service.SupportServiceImpl;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.entity.ContentType;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.connect.common.ConnectConstant.CLIENT_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.CLIENT_SECRET;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.connect.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@DisplayName("SupportServiceImpl Test")
public class SupportServiceImplTest {

  @Autowired
  private SupportServiceImpl supportServiceImpl;

  @Autowired
  private BanksaladClientSecretRepository banksaladClientSecretRepository;

  @Autowired
  private OrganizationOauthTokenRepository organizationOauthTokenRepository;

  private static WireMockServer wireMockServer;

  @BeforeAll
  public static void setupClass() {
    wireMockServer = new WireMockServer(9090);
    wireMockServer.start();
  }

  @AfterAll
  public static void clean() {
    wireMockServer.shutdown();
  }

  @Test
  @DisplayName("엑세스 토큰 발급 테스트 - 토큰이 없는경우.")
  void getAccessTokenTest_success1() {
    setupServer("SU01_001.json");
    setBanksaladClientSecret();
    String token = supportServiceImpl.getAccessToken(SecretType.FINANCE);
    assertEquals("Bearer accessToken", token);

    assertThat(organizationOauthTokenRepository.findBySecretType(SecretType.FINANCE.name()).orElse(null))
        .usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .ignoringFields("accessTokenExpiresAt")
        .isEqualTo(OrganizationOauthTokenEntity.builder()
            .secretType("FINANCE")
            .accessToken("accessToken")
            .accessTokenExpiresIn(123456789)
            .tokenType("Bearer")
            .scope("manage")
            .build()
        );
  }

  @Test
  @DisplayName("엑세스 토큰 발급 테스트 - 토큰 갱신이 필요한경우")
  void getAccessTokenTest_success2() {
    setupServer("SU01_001.json");
    setBanksaladClientSecret();
    LocalDateTime expiresAt = LocalDateTime.now().minusDays(2);
    organizationOauthTokenRepository.save(
        OrganizationOauthTokenEntity.builder()
            .secretType(SecretType.FINANCE.name())
            .accessToken("accessToken")
            .accessTokenExpiresAt(expiresAt)
            .accessTokenExpiresIn(1234)
            .tokenType("Bearer")
            .scope("manage")
            .build()
    );
    String token = supportServiceImpl.getAccessToken(SecretType.FINANCE);
    assertEquals("Bearer accessToken", token);

    List<OrganizationOauthTokenEntity> test2 = organizationOauthTokenRepository.findAll();

    assertThat(organizationOauthTokenRepository.findBySecretType(SecretType.FINANCE.name()).orElse(null))
        .usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .ignoringFields("accessTokenExpiresAt")
        .isEqualTo(OrganizationOauthTokenEntity.builder()
            .secretType("FINANCE")
            .accessToken("accessToken")
            .accessTokenExpiresIn(123456789)
            .tokenType("Bearer")
            .scope("manage")
            .build()
        );
  }

  @Test
  @DisplayName("엑세스 토큰 발급 테스트 - 토큰 갱신이 불필요한 경우")
  void getAccessTokenTest_success3() {
    setupServer("SU01_001.json");
    setBanksaladClientSecret();
    LocalDateTime expiresAt = LocalDateTime.now().plusDays(2);
    organizationOauthTokenRepository.save(
        OrganizationOauthTokenEntity.builder()
            .secretType(SecretType.FINANCE.name())
            .accessToken("accessToken")
            .accessTokenExpiresAt(expiresAt)
            .accessTokenExpiresIn(1234)
            .tokenType("Bearer")
            .scope("manage")
            .build()
    );

    String token = supportServiceImpl.getAccessToken(SecretType.FINANCE);
    assertEquals("Bearer accessToken", token);

    assertThat(organizationOauthTokenRepository.findBySecretType(SecretType.FINANCE.name()).orElse(null))
        .usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .ignoringFields()
        .isEqualTo(OrganizationOauthTokenEntity.builder()
            .secretType("FINANCE")
            .accessToken("accessToken")
            .accessTokenExpiresAt(expiresAt)
            .accessTokenExpiresIn(1234)
            .tokenType("Bearer")
            .scope("manage")
            .build()
        );
  }

  @Test
  @DisplayName("엑세스 토큰 발급 테스트 - client id가 없는경우")
  void getAccessTokenTest_fail1() {
    Exception responseException = assertThrows(
        Exception.class,
        () -> supportServiceImpl.getAccessToken(SecretType.FINANCE)
    );
    AssertionsForClassTypes.assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals("NOT_FOUND_CLIENT_ID", responseException.getMessage());
  }

  @Test
  @DisplayName("엑세스 토큰 발급 테스트 - token type, scope가 지정된 값이 아닌경우")
  void getAccessTokenTest_fail2() {
    setBanksaladClientSecret();
    setupServer("SU01_002.json");
    Exception responseException = assertThrows(
        Exception.class,
        () -> supportServiceImpl.getAccessToken(SecretType.FINANCE)
    );
    AssertionsForClassTypes.assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals("INVALID_TOKEN_RESPONSE", responseException.getMessage());

    setupServer("SU01_003.json");
    responseException = assertThrows(
        Exception.class,
        () -> supportServiceImpl.getAccessToken(SecretType.FINANCE)
    );
    AssertionsForClassTypes.assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals("INVALID_TOKEN_RESPONSE", responseException.getMessage());
  }

  @Test
  @DisplayName("엑세스 토큰 발급 테스트 - API result에 응답값이 올바르지 않은경우")
  void getAccessTokenTest_fail3() {
    setBanksaladClientSecret();
    setupFailureSituation("SU01_004.json");
    Exception responseException = assertThrows(
        Exception.class,
        () -> supportServiceImpl.getAccessToken(SecretType.FINANCE)
    );
    AssertionsForClassTypes.assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals("HTTP STATUS IS NOT OK", responseException.getMessage());
  }


  private void setBanksaladClientSecret() {
    banksaladClientSecretRepository.save(
        BanksaladClientSecretEntity.builder()
            .secretType(SecretType.FINANCE.name())
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .build()
    );
  }

  private void setupServer(String fileName) {
    // 7.1.1 접근토큰 발급
    wireMockServer.stubFor(post(urlMatching("/oauth/2.0/token"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/" + fileName))));
  }

  private void setupFailureSituation(String fileName) {
    // 7.1.1 접근토큰 발급
    wireMockServer.stubFor(post(urlMatching("/oauth/2.0/token"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/" + fileName))));
  }
}
