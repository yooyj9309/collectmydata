package com.banksalad.collectmydata.invest.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.invest.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AccountServiceImplTest {

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  private static WireMockServer wireMockServer;

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "nh_securities";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final String ACCESS_TOKEN = "accessToken";
  private static final String ORGANIZATION_CODE = "020";

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void clean() {
    wireMockServer.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("6.4.2 계좌 기본정보 조회 성공 테스트")
  void listInvestAccountBasicsTest() {

    List<AccountSummaryEntity> accountSummaryEntities = getAccountSummaryEntities();
    accountSummaryRepository.saveAll(accountSummaryEntities);

    List<AccountSummary> accountSummaries = accountSummaryEntities.stream()
        .map(accountSummaryMapper::entityToDto)
        .collect(Collectors.toList());

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken(ACCESS_TOKEN)
        .organizationHost(ORGANIZATION_HOST + ":" + wireMockServer.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    Organization organization = Organization.builder()
        .organizationCode(ORGANIZATION_CODE)
        .build();

    List<AccountBasic> accountBasics = accountService
        .listAccountBasics(executionContext, organization, accountSummaries);

    assertEquals(1, accountBasics.size());
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(post(urlMatching("/accounts/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/request/IV02_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(0)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IV02_001_single_page_00.json"))));
  }

  private List<AccountSummaryEntity> getAccountSummaryEntities() {
    return List.of(
        AccountSummaryEntity.builder()
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum("1234567890")
            .consent(true)
            .accountName("종합매매 증권계좌")
            .accountStatus("201")
            .accountType("101")
            .basicSearchTimestamp(0L)
            .transactionSyncedAt(null)
            .productSearchTimestamp(0L)
            .build()
    );
  }
}
