package com.banksalad.collectmydata.card.summary;

import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.common.db.repository.CardSummaryRepository;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.card.summary.dto.ListCardSummariesRequest;
import com.banksalad.collectmydata.card.template.ServiceTest;
import com.banksalad.collectmydata.card.template.provider.CardSummaryInvocationContextProvider;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@DisplayName("카드-001 카드 목록 조회")
public class CardSummaryServiceTest extends
    ServiceTest<Object, UserSyncStatusEntity, CardSummaryEntity, Object> {

  @Autowired
  private SummaryService<ListCardSummariesRequest, CardSummary> mainService;

  @Autowired
  private SummaryRequestHelper<ListCardSummariesRequest> requestHelper;

  @Autowired
  private SummaryResponseHelper<CardSummary> responseHelper;

  @Autowired
  private UserSyncStatusRepository parentRepository;

  @Autowired
  private CardSummaryRepository mainRepository;

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

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
  @ExtendWith(CardSummaryInvocationContextProvider.class)
  public void unitTests(TestCase<Object, UserSyncStatusEntity, CardSummaryEntity, Object> testCase)
      throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    final Integer status = testCase.getExpectedResponses().get(testCase.getExpectedResponses().size() - 1).getStatus();

    if (status != null && status != 200) { // if (mainService instanceof SummaryService)
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

    userSyncStatusEntities
        .forEach(userSyncStatusEntity -> parentRepository.save(userSyncStatusEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<CardSummaryEntity> accountSummaryEntities) {

    accountSummaryEntities
        .forEach(accountSummaryEntity -> mainRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, UserSyncStatusEntity, CardSummaryEntity, Object> testCase)
      throws ResponseNotOkException {

    mainService
        .listAccountSummaries(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateParents(List<UserSyncStatusEntity> expectedParents) {

    final List<UserSyncStatusEntity> actualParents = parentRepository.findAll();

    assertAll("*** Parent 확인 ***",
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
  protected void validateMains(List<CardSummaryEntity> expectedMains) {

    final List<CardSummaryEntity> actualMains = mainRepository.findAll();

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
