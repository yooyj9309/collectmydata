package com.banksalad.collectmydata.telecom.telecom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.telecom.collect.Executions;
import com.banksalad.collectmydata.telecom.common.db.entity.PaidTransactionEntity;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.PaidTransactionRepository;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomSummaryRepository;
import com.banksalad.collectmydata.telecom.common.mapper.PaidTransactionMapper;
import com.banksalad.collectmydata.telecom.common.mapper.TelecomSummaryMapper;
import com.banksalad.collectmydata.telecom.common.service.TelecomSummaryService;
import com.banksalad.collectmydata.telecom.summary.TelecomSummaryRequestHelper;
import com.banksalad.collectmydata.telecom.summary.TelecomSummaryResponseHelper;
import com.banksalad.collectmydata.telecom.summary.dto.ListTelecomSummariesRequest;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomPaidTransactionsRequest;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomPaidTransaction;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.banksalad.collectmydata.telecom.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest
public class TelecomPaidTransactionServiceTest {

  private static final int FIXED_DELAY = 0;
  private static final String ACCESS_TOKEN = "xxx.yyy.zzz";
  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "x-telecom";
  private static final String ORGANIZATION_CODE = "020";
  private static final String ORGANIZATION_HOST = "http://localhost";
  // TODO: finance TransactionApiServiceImpl.java가 바뀐다면 수정
  private static final LocalDateTime SYNCED_AT = DateUtil.toLocalDateTime("20210301", "011010");
  private static final int TRANSACTION_YEARMONTH = 202103;

  private static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());
  private final TelecomSummaryMapper telecomSummaryMapper = Mappers.getMapper(TelecomSummaryMapper.class);
  private final PaidTransactionMapper paidTransactionMapper = Mappers.getMapper(PaidTransactionMapper.class);

  @Autowired
  private TelecomSummaryService telecomSummaryService;
  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;
  @Autowired
  private UserSyncStatusService userSyncStatusService;

  @Autowired
  private SummaryService<ListTelecomSummariesRequest, TelecomSummary> summaryService;
  @Autowired
  private TelecomSummaryRequestHelper telecomSummaryRequestHelper;
  @Autowired
  private TelecomSummaryResponseHelper telecomSummaryResponseHelper;
  @Autowired
  private TelecomSummaryRepository telecomSummaryRepository;

  @Autowired
  private TransactionApiService<TelecomSummary, ListTelecomPaidTransactionsRequest, TelecomPaidTransaction> telecomPaidtransactionService;
  @Autowired
  private TelecomPaidTransactionRequestHelper telecomPaidTransactionRequestHelper;
  @Autowired
  private TelecomPaidTransactionResponseHelper telecomPaidTransactionResponseHelper;
  @Autowired
  private PaidTransactionRepository paidTransactionRepository;

  private static ExecutionContext executionContext;

  @BeforeEach
  public void start() {
    wiremock.start();
  }

  @AfterEach
  public void finish() {
    wiremock.resetAll();
    paidTransactionRepository.deleteAll();
  }

  @AfterAll
  public static void close() {
    wiremock.shutdown();
  }

  @Test
  public void test00_init() throws ResponseNotOkException {
    final String MGMT_ID = "1234567890";
    final long SEARCH_TIMSTAMP = 0;

    userSyncStatusRepository.deleteAll();
    telecomSummaryRepository.deleteAll();
    paidTransactionRepository.deleteAll();

    stubForListSummary(SEARCH_TIMSTAMP, "01");

    executionContext = initExecutionContext();

    summaryService
        .listAccountSummaries(executionContext, Executions.finance_telecom_summaries, telecomSummaryRequestHelper,
            telecomSummaryResponseHelper);
    TelecomSummaryEntity telecomSummaryEntity = telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtId(BANKSALAD_USER_ID, ORGANIZATION_ID, MGMT_ID)
        .orElse(null);
    assert telecomSummaryEntity != null;
    telecomSummaryEntity.setPaidTransactionSyncedAt(SYNCED_AT);
    telecomSummaryRepository.save(telecomSummaryEntity);
  }

  @Test
  @DisplayName("6.9.4 (1) 결제내역 조회: 빈 내역")
  public void test01_listPaidTransaction_single_00() {
    final String MGMT_ID = "1234567890";

    // Given
    stubForListPaidTransaction("single", "00");

    // When
    telecomPaidtransactionService
        .listTransactions(executionContext, Executions.finance_telecom_paid_transactions,
            telecomPaidTransactionRequestHelper, telecomPaidTransactionResponseHelper);
    PaidTransactionEntity paidTransactionEntity = paidTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtIdAndPaidTransactionNoAndTransactionYearMonth(
            BANKSALAD_USER_ID, ORGANIZATION_ID, MGMT_ID, 1, TRANSACTION_YEARMONTH)
        .orElse(null);

    // Then
    assertNull(paidTransactionEntity);
  }

  @Test
  @DisplayName("6.9.4 (2) 결제내역 조회: Single 첫번째 페이지")
  public void test02_listPaidTransaction_single_01() {
    final String MGMT_ID = "1234567890";

    // Given
    stubForListPaidTransaction("single", "01");

    // When
    telecomPaidtransactionService
        .listTransactions(executionContext, Executions.finance_telecom_paid_transactions,
            telecomPaidTransactionRequestHelper, telecomPaidTransactionResponseHelper);
    PaidTransactionEntity paidTransactionEntity = paidTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtIdAndPaidTransactionNoAndTransactionYearMonth(
            BANKSALAD_USER_ID, ORGANIZATION_ID, MGMT_ID, 1, TRANSACTION_YEARMONTH)
        .orElse(null);

    // Then
    assertEquals(2, paidTransactionRepository.count());
  }

  @Test
  @DisplayName("6.9.4 (3) 결제내역 조회: Multiple 페이지")
  public void test03_listPaidTransaction_multiple() {
    final String MGMT_ID = "1234567890";

    // Given
    stubForListPaidTransaction("multiple", "01");
    stubForListPaidTransaction("multiple", "02");

    // When
    telecomPaidtransactionService
        .listTransactions(executionContext, Executions.finance_telecom_paid_transactions,
            telecomPaidTransactionRequestHelper, telecomPaidTransactionResponseHelper);
    PaidTransactionEntity paidTransactionEntity = paidTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtIdAndPaidTransactionNoAndTransactionYearMonth(
            BANKSALAD_USER_ID, ORGANIZATION_ID, MGMT_ID, 1, TRANSACTION_YEARMONTH)
        .orElse(null);

    // Then
    assertEquals(3, paidTransactionRepository.count());
  }

  private static ExecutionContext initExecutionContext() {
    return ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .accessToken(ACCESS_TOKEN)
        .syncStartedAt(SYNCED_AT)
        .build();
  }

  private void stubForListSummary(long searchTimestamp, String seq) {
    wiremock.stubFor(get(urlMatching("/telecoms.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo(String.valueOf(searchTimestamp)))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/TC01_single_page_" + seq + ".json"))));
  }

  private void stubForListPaidTransaction(String paging, String seq) {
    wiremock.stubFor(post(urlMatching("/telecoms/paid-transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/request/TC04_" + paging + "_page_" + seq + ".json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/TC04_" + paging + "_page_" + seq + ".json"))));
  }
}
