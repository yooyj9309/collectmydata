package com.banksalad.collectmydata.referencebank.deposit;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.referencebank.collect.Executions;
import com.banksalad.collectmydata.referencebank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.referencebank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.referencebank.common.db.repository.DepositAccountTransactionRepository;
import com.banksalad.collectmydata.referencebank.common.enums.BankAccountType;
import com.banksalad.collectmydata.referencebank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.referencebank.deposit.dto.ListDepositAccountTransactionsRequest;
import com.banksalad.collectmydata.referencebank.summary.dto.AccountSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
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

import static com.banksalad.collectmydata.referencebank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("수신계좌 거래내역 조회 테스트")
class DepositAccountTransactionServiceTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private TransactionApiService<AccountSummary, ListDepositAccountTransactionsRequest, DepositAccountTransaction> depositAccountTransactionApiService;
  @Autowired
  private TransactionRequestHelper<AccountSummary, ListDepositAccountTransactionsRequest> depositAccountTransactionRequestHelper;
  @Autowired
  private TransactionResponseHelper<AccountSummary, DepositAccountTransaction> depositAccountTransactionResponseHelper;
  @Autowired
  private DepositAccountTransactionRepository depositAccountTransactionRepository;

  @MockBean
  AccountSummaryService accountSummaryService;
  @MockBean
  AccountSummaryRepository accountSummaryRepository;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

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
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode("020")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .accessToken("test")
        .syncStartedAt(LocalDateTime.of(2021, 07, 31, 0, 0, 0))
        .build();
  }

  @Test
  @DisplayName("수신계좌거래내역 조회")
  public void step_01_listDepositAccountTransactions_success() throws Exception {

    /* transaction mock server */
    setupServerDepositAccountTransactionsSinglePage();
    ExecutionContext executionContext = initExecutionContext();

    Mockito.when(accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID, BankAccountType.DEPOSIT))
        .thenReturn(List.of(
            AccountSummary.builder()
                .accountNum("1234567890")
                .seqno("01")
                .accountType(BankAccountType.depositAccountTypeCodes.get(0))
                .foreignDeposit(false)
                .consent(true)
                .prodName("뱅크샐러드 대박 적금")
                .build()
        ));

    Mockito.when(accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID, "1234567890", "01"))
        .thenReturn(Optional.of(
            AccountSummaryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
                .accountNum("1234567890")
                .accountStatus("01")
                .accountType("1001")
                .basicSearchTimestamp(0L)
                .detailSearchTimestamp(0L)
                .transactionSyncedAt(LocalDateTime.of(2021, 01, 01, 0, 0, 0))
                .isForeignDeposit(false)
                .isConsent(true)
                .prodName("자유입출식 계좌")
                .seqno("a123")
                .build())
        );

    List<DepositAccountTransaction> depositAccountTransactions = depositAccountTransactionApiService
        .listTransactions(executionContext, Executions.finance_bank_deposit_account_transaction,
            depositAccountTransactionRequestHelper, depositAccountTransactionResponseHelper);

    Assertions.assertThat(depositAccountTransactions.size()).isEqualTo(2);
  }

  private void setupServerDepositAccountTransactionsSinglePage() {
    wiremock.stubFor(post(urlMatching("/accounts/deposit/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA04_001_transaction_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA04_001_transaction_single_page_00.json"))));

  }

}