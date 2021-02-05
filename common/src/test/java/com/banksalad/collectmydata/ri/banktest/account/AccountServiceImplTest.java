package com.banksalad.collectmydata.ri.banktest.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.ri.bank.BankApplication;
import com.banksalad.collectmydata.ri.bank.account.AccountService;
import com.banksalad.collectmydata.ri.bank.account.dto.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {BankApplication.class})
@ComponentScan(basePackages = "com.banksalad.collectmydata.bank")
@DisplayName("계좌목록조회 테스트")
public class AccountServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost:9090";

  @Autowired
  private AccountService accountService;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().port(9090));

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
  @DisplayName("계좌목록조회: 단일페이지")
  public void step_01_getAccounts_singla_page_success() throws Exception {

    /* transaction mock server */
    setupServerAccountsSinglePage();

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    List<Account> accounts = accountService.getAccounts(executionContext);
    Assertions.assertThat(accounts.size()).isEqualTo(1);
  }

  @Test
  @DisplayName("계좌목록조회: 복수페이지")
  public void step_02_getAccounts_multi_page_success() throws Exception {

    /* transaction mock server */
    setupServerAccountsMultiPage();

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    List<Account> accounts = accountService.getAccounts(executionContext);
    Assertions.assertThat(accounts.size()).isEqualTo(3);
  }

  private void setupServerAccountsSinglePage() throws Exception {
    // 계좌목록조회 page 01
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/BA01_001_single_page_00.json"))));
  }

  private void setupServerAccountsMultiPage() throws Exception {
    // 계좌목록조회 page 01
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/BA01_001_single_page_01.json"))));

    // 계좌목록조회 page 02
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .withQueryParam("next_page", equalTo("02"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/BA01_001_single_page_02.json"))));

    // 계좌목록조회 page 03
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .withQueryParam("next_page", equalTo("03"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/BA01_001_single_page_03.json"))));
  }

  private static String readText(String fileInClassPath) {
    try {
      File file = ResourceUtils.getFile(fileInClassPath);
      return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Fail to read file", e);
    }
  }

}
