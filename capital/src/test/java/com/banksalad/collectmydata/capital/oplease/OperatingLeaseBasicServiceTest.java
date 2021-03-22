package com.banksalad.collectmydata.capital.oplease;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.TestHelper;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseRepository;
import com.banksalad.collectmydata.capital.oplease.dto.GetOperatingLeaseBasicRequest;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasic;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO1;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OperatingLeaseBasicServiceTest {

  @Autowired
  private AccountInfoService<AccountSummary, GetOperatingLeaseBasicRequest, OperatingLeaseBasic> operatingLeaseBasicService;

  @Autowired
  private AccountInfoRequestHelper<GetOperatingLeaseBasicRequest, AccountSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, OperatingLeaseBasic> responseHelper;

  @Autowired
  private OperatingLeaseRepository operatingLeaseRepository;

  @Autowired
  private OperatingLeaseHistoryRepository operatingLeaseHistoryRepository;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  private static WireMockServer wireMockServer;

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
  @DisplayName("6.7.5 운용리스 조회 성공케이스")
  public void getOperatingLeaseBasic_success() {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());
    saveAccountSummary();
    List<OperatingLeaseBasic> operatingLeaseBasics = operatingLeaseBasicService
        .listAccountInfos(context, Executions.capital_get_operating_lease_basic, requestHelper, responseHelper);

    List<OperatingLeaseEntity> operatingLeaseEntities = operatingLeaseRepository.findAll();
    List<OperatingLeaseHistoryEntity> operatingLeaseHistoryEntities = operatingLeaseHistoryRepository.findAll();
    assertEquals(1, operatingLeaseBasics.size());
    assertEquals(1, operatingLeaseEntities.size());
    assertEquals(1, operatingLeaseHistoryEntities.size());

    assertThat(operatingLeaseEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            OperatingLeaseEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .seqno(SEQNO1)
                .holderName("김뱅셀")
                .issueDate("20210210")
                .expDate("20221231")
                .repayDate("03")
                .repayMethod("01")
                .repayOrgCode("B01")
                .repayAccountNum("11022212345")
                .nextRepayDate("20211114")
                .build()
        );
  }

  public void saveAccountSummary() {
    accountSummaryRepository.save(
        AccountSummaryEntity.builder()
            .syncedAt(LocalDateTime.now())
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(ACCOUNT_NUM)
            .seqno(SEQNO1)
            .isConsent(true)
            .prodName("prodName")
            .prodName("")
            .accountType("3710")
            .accountStatus("")
            .build()
    );
  }

  private static void setupMockServer() {
    // 6.7.5 운용리스 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/oplease/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP05_001.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP05_001.json"))));
  }

}
