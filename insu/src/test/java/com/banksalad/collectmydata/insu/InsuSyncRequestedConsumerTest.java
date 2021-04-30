package com.banksalad.collectmydata.insu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanBasicRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionRepository;
import com.banksalad.collectmydata.insu.mock.MockInsuSyncRequestedConsumer;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountTransactionEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountTransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.banksalad.collectmydata.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ACCESS_TOKEN;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092"})
@Transactional
@Disabled("단위테스트 용도로만 사용가능. suite로 미동작")
class InsuSyncRequestedConsumerTest {

  private String accountNum = "accountNum1";
  private final String seqno = "1";

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().port(9090));

  @Autowired
  private LoanSummaryRepository loanSummaryRepository;

  @Autowired
  private LoanBasicRepository loanBasicRepository;

  @Autowired
  private LoanDetailRepository loanDetailRepository;

  @Autowired
  private LoanTransactionRepository loanTransactionRepository;

  @Autowired
  private IrpAccountSummaryRepository irpAccountSummaryRepository;

  @Autowired
  private IrpAccountBasicRepository irpAccountBasicRepository;

  @Autowired
  private IrpAccountDetailRepository irpAccountDetailRepository;

  @Autowired
  private IrpAccountTransactionRepository irpAccountTransactionRepository;

  @Autowired
  private MockInsuSyncRequestedConsumer mockInsuSyncRequestedConsumer;

  @MockBean
  private CollectmydataConnectClientService collectmydataConnectClientService;

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

  @DisplayName("interface insu-loan and irp test")
  @Test
  public void confirm_whenProducerSending_thenConsumerMessageReceived()
      throws JsonProcessingException, InterruptedException {

    setupInsuMockServer();

    when(collectmydataConnectClientService.getOrganization(ORGANIZATION_ID))
        .thenReturn(Organization.builder()
            .sector("sector")
            .industry("industry")
            .organizationId(ORGANIZATION_ID)
            .organizationCode(ORGANIZATION_CODE)
            .hostUrl("http://" + ORGANIZATION_HOST + ":9090")
            .build());

    when(collectmydataConnectClientService.getAccessToken(BANKSALAD_USER_ID, ORGANIZATION_ID))
        .thenReturn(OauthToken.builder()
            .accessToken(ACCESS_TOKEN)
            .consentId(CONSENT_ID)
            .scopes(List.of("insu.list", "insu.irp"))
            .build());

    SyncRequestedMessage syncRequestedMessage = SyncRequestedMessage.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncRequestType(SyncRequestType.ONDEMAND)
        .build();

    String message = objectMapper.writeValueAsString(syncRequestedMessage);
    kafkaTemplate.send(MessageTopic.insuSyncRequested, message);

//    assertTrue(mockInsuSyncRequestedConsumer.getLatch().await(5, TimeUnit.SECONDS));
    mockInsuSyncRequestedConsumer.getLatch().await();

//  Insu Test
    LoanSummaryEntity loanSummaryEntity = loanSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            accountNum).orElseThrow();
//    assertEquals(CONSENT_ID, loanSummaryEntity.getConsentId());
//    assertEquals(SYNC_REQUEST_ID, loanSummaryEntity.getSyncRequestId());

    LocalDateTime sourceAccountSummaryCreatedAt = loanSummaryEntity.getCreatedAt();
    assertNotNull(sourceAccountSummaryCreatedAt);

    LocalDateTime sourceAccountSummaryUpdatedAt = loanSummaryEntity.getUpdatedAt();
    assertNotNull(sourceAccountSummaryUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), loanSummaryEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), loanSummaryEntity.getUpdatedBy());

    LoanBasicEntity loanBasicEntity = loanBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            accountNum).orElseThrow();
//    assertEquals(CONSENT_ID, loanBasicEntity.getConsentId());
//    assertEquals(SYNC_REQUEST_ID, loanBasicEntity.getSyncRequestId());

    LocalDateTime sourceAccountBasicCreatedAt = loanBasicEntity.getCreatedAt();
    assertNotNull(sourceAccountBasicCreatedAt);

    LocalDateTime sourceAccountBasicUpdatedAt = loanBasicEntity.getUpdatedAt();
    assertNotNull(sourceAccountBasicUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), loanBasicEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), loanBasicEntity.getUpdatedBy());

    LoanDetailEntity loanDetailEntity = loanDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(
            BANKSALAD_USER_ID, ORGANIZATION_ID,
            accountNum).orElseThrow();
//    assertEquals(CONSENT_ID, loanDetailEntity.getConsentId());
//    assertEquals(SYNC_REQUEST_ID, loanDetailEntity.getSyncRequestId());

    LocalDateTime sourceAccountDetailCreatedAt = loanDetailEntity.getCreatedAt();
    assertNotNull(sourceAccountDetailCreatedAt);

    LocalDateTime sourceAccountDetailUpdatedAt = loanDetailEntity.getUpdatedAt();
    assertNotNull(sourceAccountDetailUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), loanDetailEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), loanDetailEntity.getUpdatedBy());

    LoanTransactionEntity accountTransactionEntity = loanTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeAndTransNoAndTransactionYearMonth(
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            accountNum,
            "20210102102000",
            "2",
            202101).orElseThrow();
//    assertEquals(CONSENT_ID, accountTransactionEntity.getConsentId());
//    assertEquals(SYNC_REQUEST_ID, accountTransactionEntity.getSyncRequestId());

    LocalDateTime sourceAccountTransactionCreatedAt = accountTransactionEntity.getCreatedAt();
    assertNotNull(sourceAccountTransactionCreatedAt);

    LocalDateTime sourceAccountTransactionUpdatedAt = accountTransactionEntity.getUpdatedAt();
    assertNotNull(sourceAccountTransactionUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), accountTransactionEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), accountTransactionEntity.getUpdatedBy());

//  IRP Test
    accountNum = "2222222222";
    IrpAccountSummaryEntity irpAccountSummaryEntity = irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            accountNum,
            seqno
        ).orElseThrow();
    assertEquals(CONSENT_ID, irpAccountSummaryEntity.getConsentId());
    assertEquals(SYNC_REQUEST_ID, irpAccountSummaryEntity.getSyncRequestId());

    LocalDateTime irpAccountSummaryCreatedAt = irpAccountSummaryEntity.getCreatedAt();
    assertNotNull(irpAccountSummaryCreatedAt);

    LocalDateTime irpAccountSummaryUpdatedAt = irpAccountSummaryEntity.getUpdatedAt();
    assertNotNull(irpAccountSummaryUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), irpAccountSummaryEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), irpAccountSummaryEntity.getUpdatedBy());

    IrpAccountBasicEntity irpAccountBasicEntity = irpAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            accountNum,
            seqno).orElseThrow();
    assertEquals(CONSENT_ID, irpAccountBasicEntity.getConsentId());
    assertEquals(SYNC_REQUEST_ID, irpAccountBasicEntity.getSyncRequestId());

    LocalDateTime irpAccountBasicCreatedAt = irpAccountBasicEntity.getCreatedAt();
    assertNotNull(irpAccountBasicCreatedAt);

    LocalDateTime irpAccountBasicUpdatedAt = irpAccountBasicEntity.getUpdatedAt();
    assertNotNull(irpAccountBasicUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), irpAccountBasicEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), irpAccountBasicEntity.getUpdatedBy());

    List<IrpAccountDetailEntity> irpAccountDetailEntities = irpAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoOrderByIrpDetailNoDesc(
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            accountNum,
            seqno);
    IrpAccountDetailEntity irpAccountDetailEntity = irpAccountDetailEntities.get(0);
    assertEquals(CONSENT_ID, irpAccountDetailEntity.getConsentId());
    assertEquals(SYNC_REQUEST_ID, irpAccountDetailEntity.getSyncRequestId());

    LocalDateTime irpAccountDetailCreatedAt = irpAccountDetailEntity.getCreatedAt();
    assertNotNull(irpAccountDetailCreatedAt);

    LocalDateTime irpAccountDetailUpdatedAt = irpAccountDetailEntity.getUpdatedAt();
    assertNotNull(irpAccountDetailUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), irpAccountDetailEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), irpAccountDetailEntity.getUpdatedBy());

    IrpAccountTransactionEntity irpAccountTransactionEntity = irpAccountTransactionRepository
        .findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUniqueTransNo(
            Integer.parseInt(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()).substring(0, 6)),
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            accountNum,
            seqno,
            "08cc84f01dbb29fb4e65a6a1712e0df158fb50eaa02f27114357bd0e9495f2bb").orElseThrow();

    assertEquals(CONSENT_ID, irpAccountTransactionEntity.getConsentId());
    assertEquals(SYNC_REQUEST_ID, irpAccountTransactionEntity.getSyncRequestId());

    LocalDateTime irpAccountTransactionCreatedAt = irpAccountTransactionEntity.getCreatedAt();
    assertNotNull(irpAccountTransactionCreatedAt);

    LocalDateTime irpAccountTransactionUpdatedAt = irpAccountTransactionEntity.getUpdatedAt();
    assertNotNull(irpAccountTransactionUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), irpAccountTransactionEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), irpAccountTransactionEntity.getUpdatedBy());
  }

  private void setupInsuMockServer() {

    wiremock.stubFor(get(urlMatching("/loans.*")).atPriority(1)
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/insu11/response/IS11_006_page_01.json"))));

    wiremock.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/request/IS12_002_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS12_002_page_01.json"))));

    wiremock.stubFor(post(urlMatching("/loans/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS13_002_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS13_002_page_01.json"))));

    wiremock.stubFor(post(urlMatching("/loans/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/request/IS14_005_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS14_005_page_01.json"))));
/*

    wiremock.stubFor(post(urlMatching("/loans/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/request/IS14_003_page_02.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS14_003_page_02.json"))));
*/

    wiremock.stubFor(get(urlMatching("/irps.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp11/response/IR01_002_single_page_00.json"))));

    wiremock.stubFor(post(urlMatching("/irps/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp11/request/IR02_002_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp11/response/IR02_002_single_page_00.json"))));

    wiremock.stubFor(post(urlMatching("/irps/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp11/request/IR03_003_multi_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp11/response/IR03_003_multi_page_00.json"))));

    wiremock.stubFor(post(urlMatching("/irps/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp11/request/IR03_003_multi_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp11/response/IR03_003_multi_page_01.json"))));

    wiremock.stubFor(post(urlMatching("/irps/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp11/request/IR04_003_multi_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp11/response/IR04_003_multi_page_00.json"))));

    wiremock.stubFor(post(urlMatching("/irps/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp11/request/IR04_003_multi_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp11/response/IR04_003_multi_page_01.json"))));
  }
}
