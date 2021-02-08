package com.banksalad.collectmydata.capital.common.execution;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.account.dto.AccountRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountResponse;
import com.banksalad.collectmydata.capital.common.collect.Executions;
import com.banksalad.collectmydata.capital.common.service.ExecutionService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.List;

import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("ExecutionService Test")
public class ExecutionServiceTest {

  @Autowired
  private ExecutionService executionService;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().port(9090));
  private static final String ORGANIZATION_HOST = "http://localhost:9090";

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
  @DisplayName("6.7.1 계좌 조회 execution 테스트")
  public void getAccountsTest() {
    setupServer();

    ExecutionContext executionContext = ExecutionContext.builder()
        .accessToken("accessToken")
        .organizationHost(ORGANIZATION_HOST)
        .build();

    ExecutionRequest<AccountRequest> executionRequest =
        ExecutionRequest.<AccountRequest>builder()
            .headers(new HashMap<>())
            .request(AccountRequest.builder().searchTimestamp(0L).orgCode("020").build())
            .build();

    // 6.7.1 조회
    AccountResponse response = (AccountResponse) executionService.execute(
        executionContext,
        Executions.capital_get_accounts,
        executionRequest
    );

    assertEquals(2, response.getAccountCnt());
    assertThat(response).usingRecursiveComparison().isEqualTo(
        AccountResponse.builder()
            .rspCode("000")
            .rspMsg("rsp_msg")
            .searchTimestamp(1000L)
            .regDate("20210207")
            .accountCnt(2)
            .accountList(List.of(
                Account.builder()
                    .accountNum("1234123412341234")
                    .isConsent(true)
                    .seqno(1)
                    .prodName("상품명1")
                    .accountType("3100")
                    .accountStatus("01")
                    .build(),
                Account.builder()
                    .accountNum("5678567856785678")
                    .isConsent(true)
                    .seqno(2)
                    .prodName("상품명2")
                    .accountType("3210")
                    .accountStatus("03")
                    .build()
                )
            )
    );
  }

  private void setupServer() {
    // 계좌목록조회 page 01
    wiremock.stubFor(get(urlMatching("/loans.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/CP01_001.json"))));
  }
}
