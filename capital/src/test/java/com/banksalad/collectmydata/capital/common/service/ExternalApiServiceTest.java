package com.banksalad.collectmydata.capital.common.service;


import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.account.dto.AccountRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountResponse;
import com.banksalad.collectmydata.capital.account.dto.Interest;
import com.banksalad.collectmydata.capital.account.dto.Transaction;
import com.banksalad.collectmydata.capital.account.dto.TransactionRequest;
import com.banksalad.collectmydata.capital.account.dto.TransactionResponse;
import com.banksalad.collectmydata.capital.common.collect.Executions;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("ExecutionService Test")
public class ExternalApiServiceTest {

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

  @Test
  @DisplayName("6.7.4 대출상품계좌 거래내역 조회: 빈 페이지 조회 AND seqno 없는 경우")
  public void TransactionEmptyPageTest() {
    setupServer();

    ExecutionContext executionContext = ExecutionContext.builder()
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST)
        .build();

    ExecutionRequest<TransactionRequest> executionRequest =
        ExecutionRequest.<TransactionRequest>builder()
            .headers(new HashMap<>())
            .request(TransactionRequest.builder()
                .orgCode("loanX")
                .accountNum("10041004")
                .fromDtime("20210121000000")
                .toDtime("20210122000000")
                .limit(500)
                .build())
            .build();

    TransactionResponse transactionResponse = executionService.execute(
        executionContext,
        Executions.capital_get_account_transactions,
        executionRequest
    );

    assertEquals(0, transactionResponse.getTransCnt());
    assertEquals(0, transactionResponse.getTransList().size());
    assertThat(transactionResponse).usingRecursiveComparison().isEqualTo(
        TransactionResponse.builder()
            .rspCode("00000")
            .rspMsg("rsp_msg")
            .nextPage("0")
            .transCnt(0)
            .transList(List.of())
            .build()
    );
  }

  @Test
  @DisplayName("6.7.4 대출상품계좌 거래내역 조회: 첫번째 페이지 조회 AND seqno 없는 경우")
  public void TransactionFirstPageTest() {
    setupServer();

    ExecutionContext executionContext = ExecutionContext.builder()
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST)
        .build();

    ExecutionRequest<TransactionRequest> executionRequest =
        ExecutionRequest.<TransactionRequest>builder()
            .headers(new HashMap<>())
            .request(TransactionRequest.builder()
                .orgCode("loanX")
                .accountNum("10041004")
                .fromDtime("20210121000000")
                .toDtime("20210122000000")
                .limit(2)
                .build())
            .build();

    TransactionResponse transactionResponse = executionService.execute(
        executionContext,
        Executions.capital_get_account_transactions,
        executionRequest
    );

    assertEquals(2, transactionResponse.getTransCnt());
    assertEquals(2, transactionResponse.getTransList().size());
    assertThat(transactionResponse).usingRecursiveComparison().isEqualTo(
        TransactionResponse.builder()
            .rspCode("00000")
            .rspMsg("rsp_msg")
            .nextPage("2")
            .transCnt(2)
            .transList(List.of(
                Transaction.builder()
                    .transDtime("20210121103000")
                    .transNo("trans#2")
                    .transType("03")
                    .transAmt(BigDecimal.valueOf(1000.3))
                    .balanceAmt(BigDecimal.valueOf(18000.7))
                    .principalAmt(BigDecimal.valueOf(20000.0))
                    .intAmt(BigDecimal.valueOf(100.300))
                    .intCnt(2)
                    .intList(List.of(
                        Interest.builder()
                            .intStartDate("20201201000000")
                            .intEndDate("20201231235959")
                            .intRate(BigDecimal.valueOf(4.125))
                            .intType("02")
                            .build(),
                        Interest.builder()
                            .intStartDate("20201201000000")
                            .intEndDate("20201231235959")
                            .intRate(BigDecimal.valueOf(3.025))
                            .intType("01")
                            .build()
                    ))
                    .build(),
                Transaction.builder()
                    .transDtime("20210121093000")
                    .transNo("trans#1")
                    .transType("03")
                    .transAmt(BigDecimal.valueOf(1000.3))
                    .balanceAmt(BigDecimal.valueOf(18000.7))
                    .principalAmt(BigDecimal.valueOf(20000.0))
                    .intAmt(BigDecimal.valueOf(100.300))
                    .intCnt(1)
                    .intList(List.of(
                        Interest.builder()
                            .intStartDate("20201201000000")
                            .intEndDate("20201231235959")
                            .intRate(BigDecimal.valueOf(3.025))
                            .intType("99")
                            .build()
                    ))
                    .build()
            ))
            .build()
    );

  }

  @Test
  @DisplayName("6.7.4 대출상품계좌 거래내역 조회: 첫번째 페이지 조회 AND seqno 없는 경우")
  public void TransactionNextPageTest() {
    setupServer();

    ExecutionContext executionContext = ExecutionContext.builder()
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST)
        .build();

    ExecutionRequest<TransactionRequest> executionRequest =
        ExecutionRequest.<TransactionRequest>builder()
            .headers(new HashMap<>())
            .request(TransactionRequest.builder()
                .orgCode("loanX")
                .accountNum("10041004")
                .fromDtime("20210121000000")
                .toDtime("20210122000000")
                .nextPage("2")
                .limit(2)
                .build())
            .build();

    TransactionResponse transactionResponse = executionService.execute(
        executionContext,
        Executions.capital_get_account_transactions,
        executionRequest
    );

    assertEquals(1, transactionResponse.getTransCnt());
    assertEquals(1, transactionResponse.getTransList().size());
    assertThat(transactionResponse).usingRecursiveComparison().isEqualTo(
        TransactionResponse.builder()
            .rspCode("00000")
            .rspMsg("rsp_msg")
            .transCnt(1)
            .transList(List.of(
                Transaction.builder()
                    .transDtime("20210121221000")
                    .transNo("trans#3")
                    .transType("99")
                    .transAmt(BigDecimal.valueOf(0.0))
                    .balanceAmt(BigDecimal.valueOf(18000.7))
                    .principalAmt(BigDecimal.valueOf(20000.0))
                    .intAmt(BigDecimal.valueOf(0.0))
                    .intCnt(0)
                    .intList(List.of())
                    .build()
            ))
            .build()
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

    // 6.7.4 대출상품계좌 거래내역 조회: 빈 페이지 조회 AND seqno 없는 경우
    wiremock.stubFor(post(urlMatching("/loans/transactions.*"))
        .withQueryParam("org_code", equalTo("loanX"))
        .withQueryParam("account_num", equalTo("10041004"))
//            .withQueryParam("seqno", equalTo(""))
        .withQueryParam("from_dtime", equalTo("20210121000000"))
        .withQueryParam("to_dtime", equalTo("20210122000000"))
//            .withQueryParam("next_page", equalTo(""))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withFixedDelay(500)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/CP04_001.json"))));

    // 6.7.4 대출상품계좌 거래내역 조회: 첫번째 페이지 조회 AND seqno 없는 경우
    wiremock.stubFor(post(urlMatching("/loans/transactions.*"))
        .withQueryParam("org_code", equalTo("loanX"))
        .withQueryParam("account_num", equalTo("10041004"))
//            .withQueryParam("seqno", equalTo(""))
        .withQueryParam("from_dtime", equalTo("20210121000000"))
        .withQueryParam("to_dtime", equalTo("20210122000000"))
//            .withQueryParam("next_page", equalTo(""))
        .withQueryParam("limit", equalTo("2"))
        .willReturn(
            aResponse()
                .withFixedDelay(500)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/CP04_002.json"))));

    // 6.7.4 대출상품계좌 거래내역 조회: 두번째 페이지 조회 AND seqno 없는 경우
    wiremock.stubFor(post(urlMatching("/loans/transactions.*"))
        .withQueryParam("org_code", equalTo("loanX"))
        .withQueryParam("account_num", equalTo("10041004"))
//            .withQueryParam("seqno", equalTo(""))
        .withQueryParam("from_dtime", equalTo("20210121000000"))
        .withQueryParam("to_dtime", equalTo("20210122000000"))
        .withQueryParam("next_page", equalTo("2"))
        .withQueryParam("limit", equalTo("2"))
        .willReturn(
            aResponse()
                .withFixedDelay(2)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/CP04_003.json"))));

  }
}
