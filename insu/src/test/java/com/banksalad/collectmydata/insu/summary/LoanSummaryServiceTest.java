package com.banksalad.collectmydata.insu.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesRequest;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.getExecutionContext;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LoanSummaryServiceTest {

  @Autowired
  private SummaryService<ListLoanSummariesRequest, LoanSummary> loanSummaryService;
  @Autowired
  private SummaryRequestHelper<ListLoanSummariesRequest> requestHelper;
  @Autowired
  private SummaryResponseHelper<LoanSummary> responseHelper;
  @Autowired
  private LoanSummaryRepository loanSummaryRepository;
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
  @DisplayName("6.5.8 대출 목록정보 조회 테스트 : 성공 케이스")
  public void getLoanSummariesTest_case1() throws ResponseNotOkException {
    ExecutionContext context = getExecutionContext(wireMockServer.port());
    loanSummaryService
        .listAccountSummaries(context, Executions.insurance_get_loan_summaries, requestHelper, responseHelper);

    List<LoanSummaryEntity> loanSummaryEntities = loanSummaryRepository.findAll();
    assertEquals(1, loanSummaryEntities.size());
    assertThat(loanSummaryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            LoanSummaryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .prodName("보금자리론")
                .accountNum("123456789")
                .consent(true)
                .accountType("3245")
                .accountStatus("01")
                .build()
        );
  }

  private static void setupMockServer() {
    // 6.5.8 대출 목록 조회
    wireMockServer.stubFor(get(urlMatching("/loans.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS11_001_single_page_00.json"))));
  }
}
