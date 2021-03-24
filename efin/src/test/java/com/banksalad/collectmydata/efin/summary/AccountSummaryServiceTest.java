package com.banksalad.collectmydata.efin.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.efin.collect.Executions;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryPayEntity;
import com.banksalad.collectmydata.efin.common.db.repository.AccountSummaryPayRepository;
import com.banksalad.collectmydata.efin.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.efin.common.util.TestHelper;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.efin.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.banksalad.collectmydata.efin.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.efin.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.efin.common.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.efin.common.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AccountSummaryServiceTest {

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;

  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> requestHelper;

  @Autowired
  private SummaryResponseHelper<AccountSummary> responseHelper;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private AccountSummaryPayRepository accountSummaryPayRepository;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("6.6.1 전자지급수단 목록 조회 테스트 : 성공 케이스")
  public void getAccountSummariesTest_case1() throws ResponseNotOkException {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    accountSummaryService
        .listAccountSummaries(context, Executions.finance_efin_summaries, requestHelper, responseHelper);

    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    assertEquals(1, accountSummaryEntities.size());
    assertThat(accountSummaryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            AccountSummaryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .subKey("mykakaoid")
                .accountId("11****")
                .consent(true)
                .accountStatus("01")
                .payReg(true)
                .build()
        );

    List<AccountSummaryPayEntity> accountSummaryPayEntities = accountSummaryPayRepository.findAll();
    assertEquals(3, accountSummaryPayEntities.size());
    assertThat(accountSummaryPayEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            AccountSummaryPayEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .subKey("mykakaoid")
                .accountId("11****")
                .payOrgCode("080")
                .payId("423******1")
                .primary(true)
                .build()
        );
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo(TestHelper.ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/EF01_001_single_page_00.json"))));
  }
}
