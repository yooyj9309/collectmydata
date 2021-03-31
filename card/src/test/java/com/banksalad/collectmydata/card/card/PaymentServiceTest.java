package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.dto.CardBasic;
import com.banksalad.collectmydata.card.card.dto.GetCardBasicRequest;
import com.banksalad.collectmydata.card.card.dto.ListPaymentsRequest;
import com.banksalad.collectmydata.card.card.dto.Payment;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.CardEntity;
import com.banksalad.collectmydata.card.common.db.entity.PaymentEntity;
import com.banksalad.collectmydata.card.common.db.repository.CardRepository;
import com.banksalad.collectmydata.card.common.db.repository.PaymentRepository;
import com.banksalad.collectmydata.card.common.service.CardSummaryService;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.card.util.TestHelper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

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
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentServiceTest {

  @Autowired
  private UserBaseService<ListPaymentsRequest,List<Payment>> paymentService;

  @Autowired
  private UserBaseRequestHelper<ListPaymentsRequest> requestHelper;

  @Autowired
  private UserBaseResponseHelper<List<Payment>> responseHelper;

  @Autowired
  private PaymentRepository paymentRepository;

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
  @DisplayName("6.3.6 결제정보 조회")
  public void listPayments() throws ResponseNotOkException {
    // FIXME: 정해질 테스트 케이스에 따라 작성
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    paymentService.getUserBaseInfo(context,Executions.finance_card_payment,requestHelper,responseHelper);

    List<PaymentEntity> paymentEntities = paymentRepository.findAll();
    assertEquals(2, paymentEntities.size());
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(get(urlMatching("/cards/payment.*"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD23_001_single_page_00.json"))));
  }
}
