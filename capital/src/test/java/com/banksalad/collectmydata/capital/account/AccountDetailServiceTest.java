package com.banksalad.collectmydata.capital.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.account.dto.GetAccountDetailRequest;
import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.mapper.AccountDetailHistoryMapper;
import com.banksalad.collectmydata.capital.common.mapper.AccountDetailMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.summary.AccountSummaryRequestHelper;
import com.banksalad.collectmydata.capital.summary.AccountSummaryResponseHelper;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.capital.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.repository.ApiLogRepository;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCESS_TOKEN;
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO1;
import static com.banksalad.collectmydata.capital.common.TestHelper.SYNCED_AT;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest
public class AccountDetailServiceTest {

  private static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  private final AccountDetailMapper detailMapper = Mappers.getMapper(AccountDetailMapper.class);
  private final AccountDetailHistoryMapper detailHistoryMapper = Mappers.getMapper(AccountDetailHistoryMapper.class);

  private static ExecutionContext executionContext;

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> summaryService;
  @Autowired
  private AccountSummaryRequestHelper summaryRequestHelper;
  @Autowired
  private AccountSummaryResponseHelper summaryResponseHelper;
  @Autowired
  private AccountSummaryRepository summaryRepository;

  @Autowired
  private AccountInfoService<AccountSummary, GetAccountDetailRequest, AccountDetail> detailService;
  @Autowired
  private AccountDetailRequestHelper detailRequestHelper;
  @Autowired
  private AccountDetailResponseHelper detailResponseHelper;
  @Autowired
  private AccountDetailRepository detailRepository;
  @Autowired
  private AccountDetailHistoryRepository detailHistoryRepository;

  @Autowired
  private UserSyncStatusService userSyncStatusService;
  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  @Autowired
  private AccountSummaryService accountSummaryService;

  @Autowired
  private ApiLogRepository apiLogRepository;

  @BeforeEach
  public void start() throws ResponseNotOkException {
    wiremock.start();
  }

  @AfterAll
  static void tearDown() {
    wiremock.shutdown();
  }

  @Test
  @DisplayName("6.7.3 (1) 대출상품계좌 추가정보 조회: 계좌 2개 가진 경우")
  public void cp03_001_single_page_01() throws ResponseNotOkException {
    init();

    // Given: 계좌 2개에 대해서 Mock API 서버 설정
    stubForListAccountDetails("001", "single", "01");
    stubForListAccountDetails("001", "single", "02");

    // When: finance의 list detail 메서드 호출
    detailService.listAccountInfos(executionContext, Executions.capital_get_account_detail, detailRequestHelper,
        detailResponseHelper);

    // Then
    // Check account_detail size.
    assertEquals(2, detailRepository.count());

    // Check detail_search_timestamp.
    Long searchTimestamp = summaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM,
            SEQNO1).get().getDetailSearchTimestamp();
    assertEquals(100, searchTimestamp);

    // Check account_detail and account_detail_history.
    AccountDetailEntity detailEntity = detailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM,
            SEQNO1).orElse(null);
    AccountDetailHistoryEntity detailHistoryEntity = detailHistoryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM,
            SEQNO1).get(0);
    AccountDetail accountDetail1 = detailMapper.entityToDto(detailEntity);
    AccountDetail accountDetail2 = detailHistoryMapper.entityToDto(detailHistoryEntity);
    assertTrue(ObjectComparator.isSame(accountDetail1, accountDetail2));
  }

  @Test
  @DisplayName("6.7.3 (2) 대출상품계좌 추가정보 조회: 계좌 1개 업데이트 경우")
  public void cp03_002_single_page_01() {
    // Given: 계좌 2개에 대해서 Mock API 서버 설정
    stubForListAccountDetails("002", "single", "01");
    stubForListAccountDetails("002", "single", "02");

    // When: finance의 list detail 메서드 호출
    detailService.listAccountInfos(executionContext, Executions.capital_get_account_detail, detailRequestHelper,
        detailResponseHelper);

    // Then
    // Check account_detail and account_detail_history size.
    assertEquals(2, detailRepository.count());
    assertEquals(3, detailHistoryRepository.count());

    // Check detail_search_timestamp.
    Long searchTimestamp = summaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM,
            SEQNO1).get().getDetailSearchTimestamp();
    assertEquals(300, searchTimestamp);

    // Check account_detail and account_detail_history.
    List<AccountDetailHistoryEntity> detailHistoryEntities = detailHistoryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM,
            SEQNO1);
    assertEquals(2, detailHistoryEntities.size());

    finish();
  }

  private void init() throws ResponseNotOkException {
    final long SEARCH_TIMSTAMP = 0;

    userSyncStatusRepository.deleteAll();
    detailRepository.deleteAll();
    detailHistoryRepository.deleteAll();

    stubForListSummaries("001", SEARCH_TIMSTAMP, "01");

    executionContext = initExecutionContext();

    summaryService
        .listAccountSummaries(executionContext, Executions.capital_get_accounts, summaryRequestHelper,
            summaryResponseHelper);
    AccountSummaryEntity summaryEntity = summaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM,
            SEQNO1)
        .orElse(null);
    summaryEntity.setTransactionSyncedAt(SYNCED_AT);
    summaryRepository.save(summaryEntity);
  }

  private void finish() {
    summaryRepository.deleteAll();
    apiLogRepository.deleteAll();
  }

  private ExecutionContext initExecutionContext() {
    return ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + wiremock.port())
        .accessToken(ACCESS_TOKEN)
        .syncStartedAt(SYNCED_AT)
        .build();
  }

  private void stubForListSummaries(String tc, long searchTimestamp, String seq) {
    wiremock.stubFor(get(urlMatching("/loans.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo(String.valueOf(searchTimestamp)))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP01_" + tc + "_single_page_" + seq + ".json"))));
  }

  private void stubForListAccountDetails(String tc, String paging, String seq) {
    wiremock.stubFor(post(urlMatching("/loans/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP03_" + tc + "_" + paging + "_page_" + seq + ".json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP03_" + tc + "_" + paging + "_page_" + seq + ".json"))));
  }
}
