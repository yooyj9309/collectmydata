package com.banksalad.collectmydata.insu.loan;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.common.util.TestHelper;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailRequest;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LoanDetailServiceTest {

  private static WireMockServer wireMockServer;

  @Autowired
  private AccountInfoService<LoanSummary, GetLoanDetailRequest, LoanDetail> accountInfoService;

  @Autowired
  private AccountInfoRequestHelper<GetLoanDetailRequest, LoanSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<LoanSummary, LoanDetail> responseHelper;

  @Autowired
  private LoanDetailRepository loanDetailRepository;

  @Autowired
  private LoanDetailHistoryRepository loanDetailHistoryRepository;

  @Autowired
  private LoanSummaryRepository loanSummaryRepository;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @Test
  @Transactional
  @DisplayName("6.5.10 (1) 대출상품 추가정보 조회: 신규 케이스")
  public void listLoanDetails_succeed_1_of_1() {
    ExecutionContext executionContext = TestHelper.getExecutionContext(wireMockServer.port());
    saveLoanSummaryEntity();

    accountInfoService.listAccountInfos(executionContext,
        Executions.insurance_get_loan_detail, requestHelper, responseHelper);
    List<LoanDetailEntity> loanDetailEntities = loanDetailRepository.findAll();
    List<LoanDetailHistoryEntity> loanDetailHistoryEntities = loanDetailHistoryRepository.findAll();

    assertEquals(1, loanDetailEntities.size());
    assertEquals(1, loanDetailHistoryEntities.size());

    // TODO compare with db
//    assertThat(loanDetails.get(0)).usingRecursiveComparison()
//        .isEqualTo(
//            LoanDetail.builder()
//                .currencyCode("KRW")
//                .balanceAmt(new BigDecimal("125.075"))
//                .loanPrincipal(new BigDecimal("10000.000"))
//                .nextRepayDate("20210325")
//                .build()
//        );

    assertThat(loanDetailEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            LoanDetailEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .currencyCode("KRW")
                .balanceAmt(new BigDecimal("125.075"))
                .loanPrincipal(new BigDecimal("10000.000"))
                .nextRepayDate("20210325")
                .build()
        );

    assertThat(loanDetailHistoryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            LoanDetailEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .currencyCode("KRW")
                .balanceAmt(new BigDecimal("125.075"))
                .loanPrincipal(new BigDecimal("10000.000"))
                .nextRepayDate("20210325")
                .build()
        );
  }

  private static void setupMockServer() {
    // 6.5.10 대출상품 추가정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS13_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(0)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS13_001_single_page_00.json"))));
  }

  private void saveLoanSummaryEntity() {
    loanSummaryRepository.save(LoanSummaryEntity.builder()
        .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .consent(true)
        .prodName("좋은 보험대출")
        .accountType("3400")
        .accountStatus("01")
        .detailSearchTimestamp(0L)
        .build());
  }

}
