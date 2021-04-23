package com.banksalad.collectmydata.bank.deposit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.bank.deposit.dto.ListDepositAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.bank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

@Disabled
@SpringBootTest
@DisplayName("수신계좌 거래내역 서비스 테스트")
@Transactional
class DepositAccountTransactionServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private TransactionApiService<AccountSummary, ListDepositAccountTransactionsRequest, DepositAccountTransaction> depositTransactionApiService;

  @Autowired
  private TransactionRequestHelper<AccountSummary, ListDepositAccountTransactionsRequest> depositAccountTransactionRequestHelper;

  @Autowired
  private TransactionResponseHelper<AccountSummary, DepositAccountTransaction> depositAccountTransactionResponseHelper;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private DepositAccountTransactionRepository depositAccountTransactionRepository;

  private AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

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

  @Test
  @DisplayName("수신계좌 거래내역 조회")
  public void step_01_listDepositAccountTransactions_single_page_success() {

    /* transaction mock server */
    setupServerDepositAccountTransactionsSinglePage();

    /* save mock account summaries */
    accountSummaryRepository.saveAll(getAccountSummaryEntities());

    /* execution context */
    LocalDateTime currentSyncStartedAt = LocalDateTime.of(2021, 03, 28, 0, 0, 0);
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .syncRequestId(UUID.randomUUID().toString())
        .executionRequestId(UUID.randomUUID().toString())
        .accessToken("test")
        .organizationCode("020")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .syncStartedAt(LocalDateTime.of(2021, 03, 28, 0, 0, 0))
        .build();

    depositTransactionApiService.listTransactions(executionContext, Executions.finance_bank_deposit_account_transaction,
        depositAccountTransactionRequestHelper, depositAccountTransactionResponseHelper);

    List<DepositAccountTransactionEntity> depositAccountTransactionEntities = depositAccountTransactionRepository
        .findAll();

    Assertions.assertThat(depositAccountTransactionEntities.size()).isEqualTo(2);

    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID,
            "1234567890", "a123").get();

    Assertions.assertThat(accountSummaryEntity.getTransactionSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());
  }

  private void setupServerDepositAccountTransactionsSinglePage() {
    wiremock.stubFor(post(urlMatching("/accounts/deposit/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA04_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA04_001_single_page_00.json"))));
  }

  private List<AccountSummaryEntity> getAccountSummaryEntities() {
    return List.of(
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
            .accountNum("1234567890")
            .accountStatus("01")
            .accountType("1001")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(LocalDateTime.of(2020, 03, 27, 0, 0, 0))
            .foreignDeposit(false)
            .consent(true)
            .prodName("자유입출식 계좌")
            .seqno("a123")
            .build(),
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
            .accountNum("234246541143")
            .accountStatus("01")
            .accountType("1001")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(LocalDateTime.of(2020, 03, 27, 0, 0, 0))
            .foreignDeposit(false)
            .consent(false)
            .prodName("자유입출식 계좌")
            .build()
    );
  }
}
