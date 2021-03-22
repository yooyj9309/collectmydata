package com.banksalad.collectmydata.capital.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.TestHelper;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.capital.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AccountSummaryServiceTest {

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;

  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> accountSummaryRequestHelper;

  @Autowired
  private SummaryResponseHelper<AccountSummary> accountSummaryResponseHelper;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  private static WireMockServer wireMockServer;


  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterEach
  void cleanBefore() {
    accountSummaryRepository.deleteAll();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("listAccounts 성공케이스 테스트")
  void getAccountList_test1() throws ResponseNotOkException {

    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    accountSummaryService.listAccountSummaries(
        context,
        Executions.capital_get_accounts,
        accountSummaryRequestHelper,
        accountSummaryResponseHelper
    );

    List<AccountSummaryEntity> accountListEntities = accountSummaryRepository.findAll(); // 검증

    assertEquals(2, accountListEntities.size());
    assertThat(accountListEntities.get(0)).usingRecursiveComparison()
        .ignoringFields("id", "syncedAt", "createdAt", "createdBy", "updatedAt", "updatedBy")
        .isEqualTo(
            AccountSummaryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .isConsent(true)
                .seqno("1")
                .prodName("상품명1")
                .accountType("3100")
                .accountStatus("01")
                .build()
        );
  }

  private static void setupMockServer() {
    // 6.7.1 계좌목록 조회
    wireMockServer.stubFor(get(urlMatching("/loans.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP01_001_single_page_01.json"))));
  }
}
