package com.banksalad.collectmydata.card.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

import com.banksalad.collectmydata.card.card.dto.ListPaymentsRequest;
import com.banksalad.collectmydata.card.card.dto.Payment;
import com.banksalad.collectmydata.card.common.db.entity.PaymentEntity;
import com.banksalad.collectmydata.card.common.db.repository.PaymentRepository;
import com.banksalad.collectmydata.card.template.ServiceTest;
import com.banksalad.collectmydata.card.template.provider.PaymentInvocationContextProvider;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_OK;

@SpringBootTest
@Transactional
@DisplayName("카드-006 결제정보 조회")
public class PaymentServiceTest extends ServiceTest<Object, UserSyncStatusEntity, PaymentEntity, Object> {

  @Autowired
  private UserBaseService<ListPaymentsRequest,List<Payment>> paymentService;

  @Autowired
  private UserBaseRequestHelper<ListPaymentsRequest> requestHelper;

  @Autowired
  private UserBaseResponseHelper<List<Payment>> responseHelper;

  @Autowired
  private PaymentRepository paymentRepository;

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
  static void shutdown() {
    wireMockServer.shutdown();
  }

  @TestTemplate
  @ExtendWith(PaymentInvocationContextProvider.class)
  public void unitTests(TestCase<Object, UserSyncStatusEntity, PaymentEntity, Object> testCase) throws ResponseNotOkException {

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
  protected void saveParents(List<UserSyncStatusEntity> userSyncStatusEntities) {

  }

  @Override
  protected void saveMains(List<PaymentEntity> paymentEntities) {

  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, UserSyncStatusEntity, PaymentEntity, Object> testCase)
      throws ResponseNotOkException {
    throw new ResponseNotOkException(500, "50001", "responseMessage");

  }

  @Override
  protected void validateParents(List<UserSyncStatusEntity> expectedParents) {

  }

  @Override
  protected void validateMains(List<PaymentEntity> expectedMains) {

  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
