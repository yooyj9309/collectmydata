package com.banksalad.collectmydata.capital.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.account.dto.AccountBasic;
import com.banksalad.collectmydata.capital.account.dto.GetAccountBasicRequest;
import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.TestHelper;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountBasicHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AccountBasicServiceTest {

  private static WireMockServer wireMockServer;

  @Autowired
  private AccountBasicRepository accountBasicRepository;
  @Autowired
  private AccountBasicHistoryRepository accountBasicHistoryRepository;
  @Autowired
  private AccountSummaryRepository accountSummaryRepository;
  @Autowired
  private AccountInfoService<AccountSummary, GetAccountBasicRequest, AccountBasic> accountBasicService;
  @Autowired
  private AccountInfoRequestHelper<GetAccountBasicRequest, AccountSummary> accountBasicRequestHelper;
  @Autowired
  private AccountInfoResponseHelper<AccountSummary, AccountBasic> accountBasicResponseHelper;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterEach
  void cleanBefore() {
    accountSummaryRepository.deleteAll();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("대출상품계좌 기본정보 조회 성공 케이스")
  public void getAccountBasic_success() {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());
    AccountSummaryEntity accountSummaryEntity = AccountSummaryEntity.builder()
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .seqno("1")
        .isConsent(TRUE)
        .prodName("prodName")
        .accountType("")
        .accountStatus("")
        .build();
    accountSummaryRepository.save(accountSummaryEntity);

    List<AccountBasic> accountBasics = accountBasicService
        .listAccountInfos(context, Executions.capital_get_account_basic, accountBasicRequestHelper,
            accountBasicResponseHelper);
    List<AccountBasicEntity> accountBasicEntities = accountBasicRepository.findAll();
    List<AccountBasicHistoryEntity> accountBasicHistoryEntities = accountBasicHistoryRepository.findAll();

    assertEquals(1, accountBasics.size());
    assertEquals(1, accountBasicEntities.size());
    assertEquals(1, accountBasicHistoryEntities.size());
    assertThat(accountBasics.get(0)).usingRecursiveComparison()
        .isEqualTo(AccountBasic.builder()
            .accountNum(ACCOUNT_NUM)
            .seqno("1")
            .holderName("대출차주명")
            .issueDate("20210210")
            .expDate("20221231")
            .lastOfferedRate(new BigDecimal("2.117"))
            .repayDate("03")
            .repayMethod("01")
            .repayOrgCode("B01")
            .repayAccountNum("11022212345")
            .build()
        );

    assertThat(accountBasicEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(AccountBasicEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(ACCOUNT_NUM)
            .seqno("1")
            .holderName("대출차주명")
            .issueDate("20210210")
            .expDate("20221231")
            .lastOfferedRate(new BigDecimal("2.117"))
            .repayDate("03")
            .repayMethod("01")
            .repayOrgCode("B01")
            .repayAccountNum("11022212345")
            .build()
        );

    assertThat(accountBasicHistoryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(AccountBasicEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(ACCOUNT_NUM)
            .seqno("1")
            .holderName("대출차주명")
            .issueDate("20210210")
            .expDate("20221231")
            .lastOfferedRate(new BigDecimal("2.117"))
            .repayDate("03")
            .repayMethod("01")
            .repayOrgCode("B01")
            .repayAccountNum("11022212345")
            .build()
        );
  }


  private static void setupMockServer() {
    // 6.7.2 대출상품계좌 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/request/CP02_001.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP02_001.json"))));
  }
}
