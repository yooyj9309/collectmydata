package com.banksalad.collectmydata.card.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.card.card.dto.ApprovalDomestic;
import com.banksalad.collectmydata.card.card.dto.ListApprovalDomesticRequest;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.ApprovalDomesticEntity;
import com.banksalad.collectmydata.card.common.db.repository.ApprovalDomesticRepository;
import com.banksalad.collectmydata.card.common.service.CardSummaryService;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.card.util.TestHelper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.banksalad.collectmydata.card.util.FileUtil.readText;
import static com.banksalad.collectmydata.card.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.card.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.card.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ApprovalDomesticServiceTest {

  @Autowired
  private TransactionApiService<CardSummary, ListApprovalDomesticRequest, ApprovalDomestic> transactionApiService;

  @Autowired
  private TransactionRequestHelper<CardSummary, ListApprovalDomesticRequest> requestHelper;

  @Autowired
  private TransactionResponseHelper<CardSummary, ApprovalDomestic> responseHelper;

  @Autowired
  private ApprovalDomesticRepository approvalDomesticRepository;

  @MockBean
  private CardSummaryService cardSummaryService;

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
  @DisplayName("6.3.7 국내 승인내역 조회 : 성공 케이스")
  public void getApprovalDomesticTest_case1() {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    when(cardSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID))
        .thenReturn(List.of(
            CardSummary.builder()
                .cardId("card001")
                .cardNum("123456******456")
                .consent(true)
                .cardName("하나카드01")
                .cardMember(1)
                .searchTimestamp(1000L)
                .build()
        ));

    transactionApiService
        .listTransactions(context, Executions.finance_card_approval_domestic, requestHelper, responseHelper);

    List<ApprovalDomesticEntity> approvalDomesticEntities = approvalDomesticRepository.findAll();
    assertEquals(2, approvalDomesticEntities.size());
    assertThat(approvalDomesticEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            ApprovalDomesticEntity.builder()
                .approvalYearMonth(202103)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .cardId("card001")
                .approvedNum("001")
                .status("01")
                .payType("01")
                .approvedDtime("20210301091000")
                .merchantName("스타벅스")
                .approvedAmt(BigDecimal.valueOf(5000).setScale(3, RoundingMode.CEILING))
                .totalInstallCnt(2)
                .build()
        );
    assertThat(approvalDomesticEntities.get(1)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            ApprovalDomesticEntity.builder()
                .approvalYearMonth(202103)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .cardId("card001")
                .approvedNum("002")
                .status("02")
                .payType("01")
                .approvedDtime("20210302101000")
                .cancelDtime("20210302102000")
                .merchantName("메가커피")
                .approvedAmt(BigDecimal.valueOf(15000).setScale(3, RoundingMode.CEILING))
                .build()
        );
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(get(urlMatching("/cards.*"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD03_001_single_page_00.json"))));
  }
}
