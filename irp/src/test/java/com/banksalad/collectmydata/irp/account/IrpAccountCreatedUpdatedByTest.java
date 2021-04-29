package com.banksalad.collectmydata.irp.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.banksalad.collectmydata.irp.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("개인형 IRP 계좌 정보 createdBy, updatedBy 조회")
class IrpAccountCreatedUpdatedByTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "organizationId";
  private static final String ORGANIZATION_HOST = "http://localhost";
  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired
  private IrpAccountService irpAccountService;
  @Autowired
  private IrpAccountSummaryRepository irpAccountSummaryRepository;

  @Autowired
  private IrpAccountBasicRepository irpAccountBasicRepository;

  @Autowired
  private IrpAccountDetailRepository irpAccountDetailRepository;

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @BeforeEach
  public void setupClass() {
    wiremock.start();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  @DisplayName("개인형 IRP 계좌 기본정보 createdBy, updatedBy 테스트")
  @Test
  void getIrpAccountBasics() {

    /* irp account basic mock server */
    setupServerIrpAccountBasic();

    /* save mock account summaries */
    List<IrpAccountSummaryEntity> irpAccountSummaryEntities = getIrpAccountSummaryEntities();
    irpAccountSummaryRepository.saveAll(irpAccountSummaryEntities);

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .requestedBy(String.valueOf(BANKSALAD_USER_ID))
        .build();

    irpAccountService.listIrpAccountBasics(executionContext);

    List<IrpAccountBasicEntity> irpAccountBasicEntities = irpAccountBasicRepository.findAll();

    assertThat(irpAccountBasicEntities.size()).isEqualTo(1);
    assertThat(irpAccountBasicEntities.get(0).getConsentId()).isEqualTo(CONSENT_ID);
    assertThat(irpAccountBasicEntities.get(0).getSyncRequestId()).isEqualTo(SYNC_REQUEST_ID);
    assertThat(irpAccountBasicEntities.get(0).getCreatedBy()).isEqualTo(String.valueOf(BANKSALAD_USER_ID));
    assertThat(irpAccountBasicEntities.get(0).getUpdatedBy()).isEqualTo(String.valueOf(BANKSALAD_USER_ID));
  }

  @DisplayName("개인형 IRP 계좌 추가정보 createdBy, updatedBy 테스트")
  @Test
  public void listIrpAccountDetails() {

    /* irp account detail mock server */
    setupServerIrpAccountDetailMultiPage();

    /* save mock account summaries */
    List<IrpAccountSummaryEntity> irpAccountSummaryEntities = getIrpAccountSummaryEntities();
    irpAccountSummaryRepository.saveAll(irpAccountSummaryEntities);

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .requestedBy(String.valueOf(BANKSALAD_USER_ID))
        .build();

    irpAccountService.listIrpAccountDetails(executionContext);

    List<IrpAccountDetailEntity> irpAccountDetailEntities = irpAccountDetailRepository.findAll();

    assertThat(irpAccountDetailEntities.size()).isEqualTo(3);
    assertThat(irpAccountDetailEntities.get(0).getConsentId()).isEqualTo(CONSENT_ID);
    assertThat(irpAccountDetailEntities.get(0).getSyncRequestId()).isEqualTo(SYNC_REQUEST_ID);
    assertThat(irpAccountDetailEntities.get(0).getCreatedBy()).isEqualTo(String.valueOf(BANKSALAD_USER_ID));
    assertThat(irpAccountDetailEntities.get(0).getUpdatedBy()).isEqualTo(String.valueOf(BANKSALAD_USER_ID));
  }

  private void setupServerIrpAccountBasic() {
    wiremock.stubFor(post(urlMatching("/irps/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp/request/IR02_002_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/IR02_002_single_page_00.json"))));
  }

  private void setupServerIrpAccountDetailMultiPage() {

    // 추가정보조회 page 01
    wiremock.stubFor(post(urlMatching("/irps/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp/request/IR03_003_multi_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/IR03_003_multi_page_00.json"))));

    // 추가정보조회 page 01
    wiremock.stubFor(post(urlMatching("/irps/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp/request/IR03_003_multi_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/IR03_003_multi_page_01.json"))));
  }

  private List<IrpAccountSummaryEntity> getIrpAccountSummaryEntities() {
    return List.of(
        IrpAccountSummaryEntity.builder()
            .consentId(CONSENT_ID)
            .syncRequestId(SYNC_REQUEST_ID)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
            .accountNum("100246541123")
            .accountStatus("01")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(null)
            .isConsent(true)
            .prodName("개인형 IRP 계좌1")
            .seqno("a123")
            .build(),
        IrpAccountSummaryEntity.builder()
            .consentId(CONSENT_ID)
            .syncRequestId(SYNC_REQUEST_ID)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
            .accountNum("234246541143")
            .accountStatus("01")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(null)
            .isConsent(false)
            .prodName("개인형 IRP 계좌2")
            .seqno("a124")
            .build()
    );
  }
}
