package com.banksalad.collectmydata.card.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.card.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.card.loan.dto.GetLoanSummaryRequest;
import com.banksalad.collectmydata.card.loan.dto.LoanSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.banksalad.collectmydata.card.util.FileUtil.readText;
import static com.banksalad.collectmydata.card.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.card.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.card.util.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.card.util.TestHelper.getExecutionContext;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("대출상품 목록 조회")
class LoanSummaryServiceTest {

  @Autowired
  private UserBaseService<GetLoanSummaryRequest, LoanSummary> loanSummaryService;

  @Autowired
  private UserBaseRequestHelper<GetLoanSummaryRequest> loanSummaryRequestHelper;

  @Autowired
  private UserBaseResponseHelper<LoanSummary> loanSummaryResponseHelper;

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
  @DisplayName("6.3.9 대출상품 목록 조회")
  void loanSummary_userBaseService_getUserBaseInfo_test() throws ResponseNotOkException {
    // given
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    // when
    loanSummaryService.getUserBaseInfo(executionContext, Executions.finance_loan_summary, loanSummaryRequestHelper,
        loanSummaryResponseHelper);

    List<LoanSummaryEntity> loanSummaryEntities = loanSummaryRepository.findAll();

    // then
    assertThat(loanSummaryEntities).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(
            LoanSummaryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .loanRevolving(true)
                .loanShortTerm(false)
                .loanLongTerm(true)
                .build())
        );
  }

  private static void setupMockServer() {
    // 6.3.9 대출상품 목록 조회
    wireMockServer.stubFor(get(urlMatching("/loans.*"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CD31_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD31_001_single_page_00.json"))));
  }
}
