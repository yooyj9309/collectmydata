package com.banksalad.collectmydata.capital.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OrganizationUserEntity;
import com.banksalad.collectmydata.capital.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OrganizationUserRepository;
import com.banksalad.collectmydata.capital.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AccountSummaryServiceTest {

  @Autowired
  private AccountSummaryService accountSummaryService;

  @Autowired
  private AccountListRepository accountListRepository;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  @Autowired
  private OrganizationUserRepository organizationUserRepository;

  private static WireMockServer wireMockServer;

  private static final long banksaladUserId = 1;
  private static final String organizationId = "shinhancard";
  private static final String organizationCode = "organizationCode";


  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @Test
  @Transactional
  @DisplayName("listAccounts 성공케이스 테스트")
  void getAccountList_test1() {

    LocalDateTime now = LocalDateTime.now();
    ExecutionContext context = executionContextAssembler(now);
    Organization organization = organizationAssembler();

    List<AccountSummary> accountSummaries = accountSummaryService.listAccountSummaries(context, organization);

    List<AccountSummaryEntity> accountListEntities = accountListRepository.findAll(); // 검증
    List<UserSyncStatusEntity> userSyncStatusEntities = userSyncStatusRepository.findAll(); // 검증

    assertEquals(2, accountListEntities.size());
    assertEquals(1, userSyncStatusEntities.size());

    assertThat(accountListEntities.get(0)).usingRecursiveComparison()
        .ignoringFields("id", "syncedAt", "createdAt", "createdBy", "updatedAt", "updatedBy")
        .isEqualTo(
            AccountSummaryEntity.builder()
                .banksaladUserId(banksaladUserId)
                .organizationId(organizationId)
                .accountNum("1234123412341234")
                .isConsent(true)
                .seqno("1")
                .prodName("상품명1")
                .accountType("3100")
                .accountStatus("01")
                .build()
        );

    assertThat(organizationUserRepository.findAll().get(0)).usingRecursiveComparison()
        .ignoringFields("id", "createdAt", "createdBy", "updatedAt", "updatedBy")
        .isEqualTo(
            OrganizationUserEntity.builder()
                .syncedAt(now)
                .banksaladUserId(banksaladUserId)
                .organizationId(organizationId)
                .regDate(DateUtil.toLocalDate("20210207"))
                .build()
        );

    assertThat(accountSummaries.get(0)).usingRecursiveComparison()
        .isEqualTo(
            AccountSummary.builder()
                .accountNum("1234123412341234")
                .isConsent(true)
                .seqno("1")
                .prodName("상품명1")
                .accountType("3100")
                .accountStatus("01")
                .basicSearchTimestamp(0L)
                .detailSearchTimestamp(0L)
                .operatingLeaseBasicSearchTimestamp(0L)
                .build()
        );

    assertEquals(now, userSyncStatusEntities.get(0).getSyncedAt());
    assertEquals(1000, userSyncStatusEntities.get(0).getSearchTimestamp());
  }

  @Test
  @Transactional
  @DisplayName("listAccounts 실패케이스 테스트 : rest 호출 실패 및 db저장 되는 경우가 없는경우.")
  void getAccountList_test2() {
    // TODO https://github.com/banksalad/collectmydata/pull/89 머지 후 수정.
  }


  private ExecutionContext executionContextAssembler(LocalDateTime now) {
    return ExecutionContext.builder()
        .organizationId(organizationId)
        .banksaladUserId(banksaladUserId)
        .syncStartedAt(now)
        .accessToken("accessToken")
        .organizationHost("http://localhost:" + wireMockServer.port())
        .build();
  }

  private Organization organizationAssembler() {
    return Organization.builder()
        .organizationCode(organizationCode)
        .build();
  }

  private static void setupMockServer() {
    // 6.7.1 계좌목록 조회
    wireMockServer.stubFor(get(urlMatching("/loans.*"))
        .withQueryParam("org_code", equalTo(organizationCode))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP01_001.json"))));
  }

}
