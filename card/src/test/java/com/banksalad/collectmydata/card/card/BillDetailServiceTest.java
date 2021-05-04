package com.banksalad.collectmydata.card.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.BillDetail;
import com.banksalad.collectmydata.card.card.dto.ListBillBasicRequest;
import com.banksalad.collectmydata.card.card.dto.ListBillDetailRequest;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.BillDetailEntity;
import com.banksalad.collectmydata.card.common.db.entity.BillEntity;
import com.banksalad.collectmydata.card.common.db.repository.BillDetailRepository;
import com.banksalad.collectmydata.card.template.ServiceTest;
import com.banksalad.collectmydata.card.template.provider.BillDetailInvocationContextProvider;
import com.banksalad.collectmydata.card.util.TestHelper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.bill.BillService;
import com.banksalad.collectmydata.finance.api.bill.BillTransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.bill.BillTransactionResponseHelper;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.Lists;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.banksalad.collectmydata.card.util.FileUtil.readText;
import static com.banksalad.collectmydata.card.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.card.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.card.util.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_OK;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@DisplayName("카드-005 청구 추가정보 조회")
public class BillDetailServiceTest extends ServiceTest<Object, BillEntity, BillDetailEntity, Object> {

  @Autowired
  private BillService<ListBillBasicRequest, BillBasic, ListBillDetailRequest, BillDetail> billService;

  @Autowired
  private BillTransactionRequestHelper<ListBillDetailRequest, BillBasic> transactionRequestHelper;

  @Autowired
  private BillTransactionResponseHelper<BillBasic, BillDetail> transactionResponseHelper;

  @Autowired
  private BillDetailRepository billDetailRepository;

  private static WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeAll
  static void setup() {
    wireMockServer.start();
  }

  @AfterEach
  void tearDown() {
    wireMockServer.resetAll();
  }

  @AfterAll
  static void shutDown() {
    wireMockServer.shutdown();
  }

  @TestTemplate
  @ExtendWith(BillDetailInvocationContextProvider.class)
  public void unitTests(TestCase<Object, BillEntity, BillDetailEntity, Object> testCase) throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    final Integer status = testCase.getExpectedResponses().get(testCase.getExpectedResponses().size() - 1).getStatus();

    if (status != null && status != STATUS_OK) { // if (mainService instanceof SummaryService)
      runAndTestException(testCase);
    } else {
      runMainService(testCase);
    }
    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<BillEntity> billEntities) {

  }

  @Override
  protected void saveMains(List<BillDetailEntity> billDetailEntities) {

  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, BillEntity, BillDetailEntity, Object> testCase)
      throws ResponseNotOkException {
    throw new ResponseNotOkException(500, "50001", "responseMessage");
  }

  @Override
  protected void validateParents(List<BillEntity> expectedParents) {

  }

  @Override
  protected void validateMains(List<BillDetailEntity> expectedMains) {

  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
