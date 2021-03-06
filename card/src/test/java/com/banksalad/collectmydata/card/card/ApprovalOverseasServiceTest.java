package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.dto.ApprovalOverseas;
import com.banksalad.collectmydata.card.card.dto.ListApprovalOverseasRequest;
import com.banksalad.collectmydata.card.common.db.entity.ApprovalOverseasEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.common.db.repository.ApprovalOverseasRepository;
import com.banksalad.collectmydata.card.common.db.repository.CardSummaryRepository;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.card.template.ServiceTest;
import com.banksalad.collectmydata.card.template.provider.ApprovalOverseasInvocationContextProvider;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@DisplayName("??????-008 ?????? ???????????? ??????")
public class ApprovalOverseasServiceTest extends
    ServiceTest<Object, CardSummaryEntity, ApprovalOverseasEntity, Object> {

  @Autowired
  private TransactionApiService<CardSummary, ListApprovalOverseasRequest, ApprovalOverseas> mainService;

  @Autowired
  private TransactionRequestHelper<CardSummary, ListApprovalOverseasRequest> requestHelper;

  @Autowired
  private TransactionResponseHelper<CardSummary, ApprovalOverseas> responseHelper;

  @Autowired
  private CardSummaryRepository parentRepository;

  @Autowired
  private ApprovalOverseasRepository mainRepository;

  private static WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeAll
  static void setUp() {

    wireMockServer.start();
  }

  @AfterAll
  static void shutDown() {

    wireMockServer.shutdown();
  }

  @AfterEach
  void tearDown() {

    wireMockServer.resetAll();
  }

  @TestTemplate
  @ExtendWith(ApprovalOverseasInvocationContextProvider.class)
  public void unitTests(TestCase<Object, CardSummaryEntity, ApprovalOverseasEntity, Object> testCase) {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<CardSummaryEntity> cardSummaryEntities) {

    /* DB save()??? ????????? testCase??? summaries??? ??????????????? ???????????? ???????????? ??????. */
    cardSummaryEntities
        .forEach(cardSummaryEntity -> parentRepository.save(cardSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<ApprovalOverseasEntity> depositAccountBasicEntities) {

    depositAccountBasicEntities
        .forEach(depositAccountBasicEntity -> mainRepository.save(depositAccountBasicEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(
      TestCase<Object, CardSummaryEntity, ApprovalOverseasEntity, Object> testCase) {

    mainService
        .listTransactions(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateParents(List<CardSummaryEntity> expectedParents) {

    final List<CardSummaryEntity> actualParents = parentRepository.findAll();

    assertAll("*** Parent ?????? ***",
        () -> assertEquals(expectedParents.size(), actualParents.size()),
        () -> {
          for (int i = 0; i < expectedParents.size(); i++) {
            assertThat(actualParents.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedParents.get(i));
          }
        }
    );
  }

  @Override
  protected void validateMains(List<ApprovalOverseasEntity> expectedMains) {

    final List<ApprovalOverseasEntity> actualMains = mainRepository.findAll();

    assertAll("*** Main ?????? ***",
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
