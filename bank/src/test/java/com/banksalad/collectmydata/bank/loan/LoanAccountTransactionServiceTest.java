package com.banksalad.collectmydata.bank.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountTransactionInterestRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.enums.BankAccountType;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.loan.dto.ListLoanAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
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
@DisplayName("대출 계좌 거래내역 정보 서비스 테스트")
public class LoanAccountTransactionServiceTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  @Autowired
  private TransactionApiService<AccountSummary, ListLoanAccountTransactionsRequest, LoanAccountTransaction> loanTransactionApiService;
  @Autowired
  private TransactionRequestHelper<AccountSummary, ListLoanAccountTransactionsRequest> loanAccountTransactionRequestHelper;
  @Autowired
  private TransactionResponseHelper<AccountSummary, LoanAccountTransaction> loanAccountTransactionResponseHelper;

  @Autowired
  private LoanAccountTransactionRepository loanAccountTransactionRepository;
  @Autowired
  private LoanAccountTransactionInterestRepository loanAccountTransactionInterestRepository;

  @MockBean
  private AccountSummaryService accountSummaryService;
  @MockBean
  private AccountSummaryRepository accountSummaryRepository;

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
  @DisplayName("대출 계좌 상품 거래내역 조회")
  public void step_01_listLoanAccountTransactions_single_page_success() throws Exception {
    wiremock.start();
    /* transaction mock server */
    setupServerLoanAccountTransactionsSinglePage();

    ExecutionContext executionContext = initExecutionContext();

    Mockito
        .when(accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID, BankAccountType.LOAN))
        .thenReturn(List.of(
            AccountSummary.builder()
                .accountNum("1234567890")
                .seqno("01")
                .accountType(BankAccountType.loanAccountTypeCodes.get(0))
                .foreignDeposit(false)
                .consent(true)
                .prodName("뱅크샐러드 대박 적금")
                .build()
        ));

    Mockito.when(accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID, "1234567890",
            "01"))
        .thenReturn(Optional.of(
            AccountSummaryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
                .accountNum("1234567890")
                .accountStatus("01")
                .accountType("3001")
                .basicSearchTimestamp(0L)
                .detailSearchTimestamp(0L)
                .transactionSyncedAt(LocalDateTime.of(2021, 01, 01, 0, 0, 0))
                .foreignDeposit(false)
                .consent(true)
                .prodName("자유입출식 계좌")
                .seqno("1")
                .build())
        );

    /* assertions transaction size */
    List<LoanAccountTransaction> loanAccountTransactions = loanTransactionApiService.listTransactions(
        executionContext, Executions.finance_bank_loan_account_transaction, loanAccountTransactionRequestHelper,
        loanAccountTransactionResponseHelper
    );

    assertThat(loanAccountTransactionRepository.findAll().size()).isEqualTo(2);
    assertThat(loanAccountTransactionInterestRepository.findAll().size()).isEqualTo(3);
  }


  private void setupServerLoanAccountTransactionsSinglePage() {
    wiremock.stubFor(post(urlMatching("/accounts/loan/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA10_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA10_001_single_page_00.json"))));
  }

}
