package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.BillDetail;
import com.banksalad.collectmydata.card.card.dto.ListBillBasicRequest;
import com.banksalad.collectmydata.card.card.dto.ListBillDetailRequest;
import com.banksalad.collectmydata.card.common.db.entity.BillEntity;
import com.banksalad.collectmydata.card.common.db.repository.BillRepository;
import com.banksalad.collectmydata.card.template.ServiceTest;
import com.banksalad.collectmydata.card.template.provider.BillBasicInvocationContextProvider;
import com.banksalad.collectmydata.finance.api.bill.BillRequestHelper;
import com.banksalad.collectmydata.finance.api.bill.BillResponseHelper;
import com.banksalad.collectmydata.finance.api.bill.BillService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.IGNORING_ENTITY_FIELDS;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@DisplayName("카드-004 청구 기본정보 조회")
public class BillBasicServiceTest extends ServiceTest<Object, Object, BillEntity, Object> {

  @Autowired
  private BillService<ListBillBasicRequest, BillBasic, ListBillDetailRequest, BillDetail> mainService;

  @Autowired
  private BillRequestHelper<ListBillBasicRequest> requestHelper;

  @Autowired
  private BillResponseHelper<BillBasic> responseHelper;

  @Autowired
  private BillRepository mainRepository;

  private static WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeAll
  static void setUp() {

    wireMockServer.start();
  }

  @AfterEach
  void tearDown() {

    wireMockServer.resetAll();
  }

  @AfterAll
  static void shutDown() {

    wireMockServer.stop();
  }

  @TestTemplate
  @ExtendWith(BillBasicInvocationContextProvider.class)
  public void unitTests(TestCase<Object, Object, BillEntity, Object> testCase) throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    final Integer status = testCase.getExpectedResponses().get(testCase.getExpectedResponses().size() - 1).getStatus();

//    if (status != null && status != STATUS_OK) { // if (mainService instanceof SummaryService)
//      runAndTestException(testCase);
//    } else {
      runMainService(testCase);
//    }

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<Object> parents) {

  }

  @Override
  protected void saveMains(List<BillEntity> billEntities) {

    billEntities
        .forEach(billEntity -> mainRepository.save(billEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(
      TestCase<Object, Object, BillEntity, Object> testCase) throws ResponseNotOkException {

    mainService
        .listBills(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateParents(List<Object> expectedParents) {

  }

  @Override
  protected void validateMains(List<BillEntity> expectedMains) {

    final List<BillEntity> actualMains = mainRepository.findAll();

    assertAll("*** Main 확인 ***",
        () -> assertEquals(expectedMains.size(), actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMains.size(); i++) {
            assertThat(actualMains.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedMains.get(i));
          }
        }
    );
  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
