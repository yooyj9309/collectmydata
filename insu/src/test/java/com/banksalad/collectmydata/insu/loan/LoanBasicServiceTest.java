package com.banksalad.collectmydata.insu.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanBasicHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanBasicRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.common.util.TestHelper;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicRequest;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
public class LoanBasicServiceTest {

  private static WireMockServer wireMockServer;

  @Autowired
  private AccountInfoService<LoanSummary, GetLoanBasicRequest, LoanBasic> accountInfoService;

  @Autowired
  private AccountInfoRequestHelper<GetLoanBasicRequest, LoanSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<LoanSummary, LoanBasic> responseHelper;

  @Autowired
  private LoanBasicRepository loanBasicRepository;

  @Autowired
  private LoanBasicHistoryRepository loanBasicHistoryRepository;

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
  @DisplayName("6.5.9 (1) 대출상품 추가정보 조회: 신규 케이스")
  public void listLoanBasics_succeed_1_of_1() {
    ExecutionContext executionContext = TestHelper.getExecutionContext(wireMockServer.port());
    saveLoanSummaryEntity();

    List<LoanBasic> loanBasics = accountInfoService
        .listAccountInfos(executionContext, Executions.insurance_get_loan_basic, requestHelper, responseHelper);
    List<LoanBasicEntity> loanBasicEntities = loanBasicRepository.findAll();
    List<LoanBasicHistoryEntity> loanBasicHistoryEntities = loanBasicHistoryRepository.findAll();

    assertEquals(1, loanBasics.size());
    assertEquals(1, loanBasicEntities.size());
    assertEquals(1, loanBasicHistoryEntities.size());

    assertThat(loanBasics.get(0)).usingRecursiveComparison()
        .isEqualTo(
            LoanBasic.builder()
                .loanStartDate("20210305")
                .loanExpDate("20300506")
                .repayMethod("03")
                .insuNum("123456789")
                .build()
        );

    assertThat(loanBasics.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            LoanBasicEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .loanStartDate("20210305")
                .loanExpDate("20300506")
                .repayMethod("03")
                .insuNum("123456789")
                .build()
        );

    assertThat(loanBasicHistoryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            LoanBasicHistoryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .loanStartDate("20210305")
                .loanExpDate("20300506")
                .repayMethod("03")
                .insuNum("123456789")
                .build()
        );
  }

  private static void setupMockServer() {
    // 6.5.9 대출상품 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS12_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS12_001_single_page_00.json"))));
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
        .build());
  }

}
