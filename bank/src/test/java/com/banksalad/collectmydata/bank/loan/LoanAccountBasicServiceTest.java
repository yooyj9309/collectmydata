package com.banksalad.collectmydata.bank.loan;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.enums.BankAccountType;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountBasicRequest;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountBasic;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.banksalad.collectmydata.bank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("대출 계좌 기본 정보 서비스 테스트")
public class LoanAccountBasicServiceTest {

  public static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";


  @Autowired
  private AccountInfoService<AccountSummary, GetLoanAccountBasicRequest, LoanAccountBasic> loanAccountBasicApiService;

  @Autowired
  private AccountInfoRequestHelper<GetLoanAccountBasicRequest, AccountSummary> loanAccountBasicInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, LoanAccountBasic> loanAccountInfoBasicResponseHelper;

  @MockBean
  private AccountSummaryService accountSummaryService;

  @Autowired
  private LoanAccountBasicRepository loanAccountBasicRepository;

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

  private ExecutionContext initExecutionContext() {
    return ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .syncRequestId(UUID.randomUUID().toString())
        .executionRequestId(UUID.randomUUID().toString())
        .accessToken("test")
        .organizationCode("020")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  @Test
  @DisplayName("대출 상품 기본 정보 조회")
  public void step_01_listLoanAccountBasic_success() throws Exception {
    // Loan account basic mock server
    setupServerLoanAccountBasic();

    ExecutionContext executionContext = initExecutionContext();

    Mockito
        .when(accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID, BankAccountType.LOAN))
        .thenReturn(getAccountSummaries());

    loanAccountBasicApiService.listAccountInfos(
        executionContext,
        Executions.finance_bank_loan_account_basic,
        loanAccountBasicInfoRequestHelper,
        loanAccountInfoBasicResponseHelper
    );

    Optional<LoanAccountBasicEntity> loanAccountBasicEntity = loanAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            "1234567890",
            "1"
        );

    assertThat(loanAccountBasicEntity.isPresent()).isTrue();
  }

  private void setupServerLoanAccountBasic() throws Exception {
    wiremock.stubFor(post(urlMatching("/accounts/loan/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA21_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA21_001_single_page_00.json"))));
  }

  private List<AccountSummary> getAccountSummaries() {
    return List.of(
        AccountSummary.builder()
            .accountNum("1234567890")
            .accountStatus("01")
            .accountType("3001")
            .foreignDeposit(false)
            .consent(true)
            .prodName("자유입출식 계좌")
            .seqno("1")
            .build()
    );
  }
}
