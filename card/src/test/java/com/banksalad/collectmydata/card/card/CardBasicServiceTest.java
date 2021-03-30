package com.banksalad.collectmydata.card.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.card.card.dto.CardBasic;
import com.banksalad.collectmydata.card.card.dto.GetCardBasicRequest;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.CardEntity;
import com.banksalad.collectmydata.card.common.db.repository.CardRepository;
import com.banksalad.collectmydata.card.common.service.CardSummaryService;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.card.util.TestHelper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.banksalad.collectmydata.card.util.FileUtil.readText;
import static com.banksalad.collectmydata.card.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.card.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.card.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CardBasicServiceTest {

  @Autowired
  private AccountInfoService<CardSummary, GetCardBasicRequest, CardBasic> accountInfoService;

  @Autowired
  private AccountInfoRequestHelper<GetCardBasicRequest, CardSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<CardSummary, CardBasic> responseHelper;

  @Autowired
  private CardRepository cardRepository;

  @MockBean
  private CardSummaryService cardSummaryService;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("6.3.2 카드 기본정보 조회 : 성공 케이스")
  public void getCardBasicTest_case1() {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    when(cardSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID))
        .thenReturn(List.of(
            CardSummary.builder()
                .cardId("card001")
                .cardNum("123456******456")
                .consent(true)
                .cardName("하나카드01")
                .cardMember(1)
                .searchTimestamp(1000L)
                .build()
        ));

    accountInfoService
        .listAccountInfos(context, Executions.finance_card_basic, requestHelper, responseHelper);

    List<CardEntity> cardEntities = cardRepository.findAll();
    assertEquals(1, cardEntities.size());
    assertThat(cardEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            CardEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .cardId("card001")
                .cardType("01")
                .transPayable(true)
                .cashCard(false)
                .linkedBankCode("080")
                .cardBrand("VISA")
                .annualFee(BigDecimal.valueOf(10000).setScale(3, RoundingMode.CEILING))
                .issueDate("20210301")
                .build()
        );
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(get(urlMatching("/cards.*"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD02_001_single_page_00.json"))));
  }
}
