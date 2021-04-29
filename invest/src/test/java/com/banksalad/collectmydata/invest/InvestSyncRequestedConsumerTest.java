package com.banksalad.collectmydata.invest;

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
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.invest.common.db.entity.OrganizationUserEntity;
import com.banksalad.collectmydata.invest.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountProductRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.invest.common.db.repository.OrganizationUserRepository;
import com.banksalad.collectmydata.invest.mock.MockInvestSyncRequestedConsumer;
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
import java.util.concurrent.TimeUnit;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092"})
@Transactional
@Disabled("단위테스트 용도로만 사용가능. suite로 미동작")
class InvestSyncRequestedConsumerTest {

  private final String accountNum = "2222222222";
  private final String seqno = "1";

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().port(9090));

  @Autowired
  private OrganizationUserRepository organizationUserRepository;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private AccountBasicRepository accountBasicRepository;

  @Autowired
  private AccountProductRepository accountProductRepository;

  @Autowired
  private AccountTransactionRepository accountTransactionRepository;

  @Autowired
  private IrpAccountSummaryRepository irpAccountSummaryRepository;

  @Autowired
  private IrpAccountBasicRepository irpAccountBasicRepository;

  @Autowired
  private IrpAccountDetailRepository irpAccountDetailRepository;

  @Autowired
  private IrpAccountTransactionRepository irpAccountTransactionRepository;

  @Autowired
  private MockInvestSyncRequestedConsumer mockInvestSyncRequestedConsumer;

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

  @DisplayName("interface invest and irp test")
  @Test
  public void confirm_whenProducerSending_thenConsumerMessageReceived()
      throws JsonProcessingException, InterruptedException {

    setupInvestMockServer();

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
            .scopes(List.of("invest.list", "invest.irp"))
            .build());

    SyncRequestedMessage syncRequestedMessage = SyncRequestedMessage.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncRequestType(SyncRequestType.ONDEMAND)
        .build();

    String message = objectMapper.writeValueAsString(syncRequestedMessage);
    kafkaTemplate.send(MessageTopic.investSyncRequested, message);

    assertTrue(mockInvestSyncRequestedConsumer.getLatch().await(5, TimeUnit.SECONDS));

    OrganizationUserEntity organizationUserEntity = organizationUserRepository
        .findByBanksaladUserIdAndOrganizationId(BANKSALAD_USER_ID,
            ORGANIZATION_ID).orElseThrow();

    assertEquals(CONSENT_ID, organizationUserEntity.getConsentId());
    assertEquals(SYNC_REQUEST_ID, organizationUserEntity.getSyncRequestId());

    LocalDateTime sourceOrganizationCreatedAt = organizationUserEntity.getCreatedAt();
    assertNotNull(sourceOrganizationCreatedAt);

    LocalDateTime sourceOrganizationUpdatedAt = organizationUserEntity.getUpdatedAt();
    assertNotNull(sourceOrganizationUpdatedAt);

    String sourceCreatedBy = organizationUserEntity.getCreatedBy();
    assertEquals(String.valueOf(BANKSALAD_USER_ID), sourceCreatedBy);

    String sourceUpdatedBy = organizationUserEntity.getUpdatedBy();
    assertEquals(String.valueOf(BANKSALAD_USER_ID), sourceUpdatedBy);

//  Invest Test
    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            accountNum).orElseThrow();
    assertEquals(CONSENT_ID, accountSummaryEntity.getConsentId());
    assertEquals(SYNC_REQUEST_ID, accountSummaryEntity.getSyncRequestId());

    LocalDateTime sourceAccountSummaryCreatedAt = accountSummaryEntity.getCreatedAt();
    assertNotNull(sourceAccountSummaryCreatedAt);

    LocalDateTime sourceAccountSummaryUpdatedAt = accountSummaryEntity.getUpdatedAt();
    assertNotNull(sourceAccountSummaryUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), accountSummaryEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), accountSummaryEntity.getUpdatedBy());

    AccountBasicEntity accountBasicEntity = accountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            accountNum).orElseThrow();
    assertEquals(CONSENT_ID, accountBasicEntity.getConsentId());
    assertEquals(SYNC_REQUEST_ID, accountBasicEntity.getSyncRequestId());

    LocalDateTime sourceAccountBasicCreatedAt = accountBasicEntity.getCreatedAt();
    assertNotNull(sourceAccountBasicCreatedAt);

    LocalDateTime sourceAccountBasicUpdatedAt = accountBasicEntity.getUpdatedAt();
    assertNotNull(sourceAccountBasicUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), accountBasicEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), accountBasicEntity.getUpdatedBy());

    List<AccountProductEntity> accountProductEntities = accountProductRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(
            BANKSALAD_USER_ID, ORGANIZATION_ID,
            accountNum);
    AccountProductEntity accountProductEntity = accountProductEntities.get(0);
    assertEquals(CONSENT_ID, accountProductEntity.getConsentId());
    assertEquals(SYNC_REQUEST_ID, accountProductEntity.getSyncRequestId());

    LocalDateTime sourceAccountDetailCreatedAt = accountProductEntity.getCreatedAt();
    assertNotNull(sourceAccountDetailCreatedAt);

    LocalDateTime sourceAccountDetailUpdatedAt = accountProductEntity.getUpdatedAt();
    assertNotNull(sourceAccountDetailUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), accountProductEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), accountProductEntity.getUpdatedBy());

    AccountTransactionEntity accountTransactionEntity = accountTransactionRepository
        .findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndUniqueTransNo(
            202101,
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            accountNum,
            "66e96aece4de23a3961350d7f6463e7fdaebc823d554473c18e6b316c4dc0ff4").orElseThrow();
    assertEquals(CONSENT_ID, accountTransactionEntity.getConsentId());
    assertEquals(SYNC_REQUEST_ID, accountTransactionEntity.getSyncRequestId());

    LocalDateTime sourceAccountTransactionCreatedAt = accountTransactionEntity.getCreatedAt();
    assertNotNull(sourceAccountTransactionCreatedAt);

    LocalDateTime sourceAccountTransactionUpdatedAt = accountTransactionEntity.getUpdatedAt();
    assertNotNull(sourceAccountTransactionUpdatedAt);

    assertEquals(String.valueOf(BANKSALAD_USER_ID), accountTransactionEntity.getCreatedBy());
    assertEquals(String.valueOf(BANKSALAD_USER_ID), accountTransactionEntity.getUpdatedBy());

//  IRP Test
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

  private void setupInvestMockServer() {

    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IV01_004_page_01.json"))));

    wiremock.stubFor(post(urlMatching("/accounts/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/request/IV02_002_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IV02_002_page_01.json"))));

    wiremock.stubFor(post(urlMatching("/accounts/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/invest11/request/IV03_003_multi_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/invest11/response/IV03_003_multi_page_01.json"))));

    wiremock.stubFor(post(urlMatching("/accounts/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/invest11/request/IV03_003_multi_page_02.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/invest11/response/IV03_003_multi_page_02.json"))));

    wiremock.stubFor(post(urlMatching("/accounts/products"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/invest11/request/IV04_002_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/invest11/response/IV04_002_single_page_01.json"))));

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
