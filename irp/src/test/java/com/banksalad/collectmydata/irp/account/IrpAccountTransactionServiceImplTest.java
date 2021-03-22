package com.banksalad.collectmydata.irp.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.irp.TestConfig;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransaction;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransactionRequest;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
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

import static com.banksalad.collectmydata.irp.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("개인형 IRP계좌 거래내역 조회 테스트")
class IrpAccountTransactionServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "organizationId";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private TransactionApiService<IrpAccountSummary, IrpAccountTransactionRequest, IrpAccountTransaction> irpAccountTransactionApiService;

  @Autowired
  private TransactionRequestHelper<IrpAccountSummary, IrpAccountTransactionRequest> irpAccountTransactionRequestHelper;

  @Autowired
  private TransactionResponseHelper<IrpAccountSummary, IrpAccountTransaction> irpAccountTransactionResponseHelper;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  @MockBean
  IrpAccountSummaryService irpAccountSummaryService;

  @MockBean
  IrpAccountSummaryRepository irpAccountSummaryRepository;

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
  void listTransactions() {

    String accountNum = "1234123412341234";

    /* transaction mock server */
    setupServerIrpAccountTransactionsMultiPage();

    ExecutionContext executionContext = initExecutionContext();

    Mockito
        .when(irpAccountSummaryService
            .listConsentedAccountSummaries(BANKSALAD_USER_ID, ORGANIZATION_ID))
        .thenReturn(List.of(
            IrpAccountSummary.builder()
                .prodName("상품명1")
                .accountNum(accountNum)
                .seqno("a123")
                .consent(true)
                .accountStatus("01").build()
        ));

    Mockito.when(irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID, accountNum,
            "a123"))
        .thenReturn(Optional.of(
            IrpAccountSummaryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
                .accountNum(accountNum)
                .accountStatus("01")
                .basicSearchTimestamp(0L)
                .detailSearchTimestamp(0L)
                .transactionSyncedAt(LocalDateTime.of(2021, 01, 01, 0, 0, 0))
                .isConsent(true)
                .prodName("상품명1")
                .seqno("1")
                .build())
        );

    List<IrpAccountTransaction> depositAccountTransactions = irpAccountTransactionApiService
        .listTransactions(executionContext, Executions.irp_get_transactions,
            irpAccountTransactionRequestHelper, irpAccountTransactionResponseHelper);

    assertThat(depositAccountTransactions.size()).isEqualTo(4);
  }

  private void setupServerIrpAccountTransactionsMultiPage() {

    // IRP 거래내역정보조회 page 01
    wiremock.stubFor(post(urlMatching("/irps/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp/request/IR04_001_transaction_multi_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/IR04_001_transaction_multi_page_00.json"))));

    // IRP 거래내역정보조회 page 02
    wiremock.stubFor(post(urlMatching("/irps/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp/request/IR04_001_transaction_multi_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/IR04_001_transaction_multi_page_01.json"))));
  }
}
