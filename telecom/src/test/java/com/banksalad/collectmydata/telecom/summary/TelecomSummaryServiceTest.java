package com.banksalad.collectmydata.telecom.summary;

import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomSummaryRepository;
import com.banksalad.collectmydata.telecom.summary.dto.ListTelecomSummariesRequest;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.template.ServiceTest;
import com.banksalad.collectmydata.telecom.template.provider.TelecomSummaryInvocationContextProvider;

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
@DisplayName("통신-001 통신 계약 목록 조회")
public class TelecomSummaryServiceTest extends
    ServiceTest<Object, UserSyncStatusEntity, TelecomSummaryEntity, Object> {

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired
  private SummaryService<ListTelecomSummariesRequest, TelecomSummary> mainService;
  @Autowired
  private SummaryRequestHelper<ListTelecomSummariesRequest> requestHelper;
  @Autowired
  private SummaryResponseHelper<TelecomSummary> responseHelper;
  @Autowired
  private UserSyncStatusRepository parentRepository;
  @Autowired
  private TelecomSummaryRepository mainRepository;

  @BeforeAll
  static void setUp() {

    wireMockServer.start();
  }

  @AfterAll
  static void shutDown() {

    wireMockServer.stop();
  }

  @AfterEach
  void tearDown() {

    wireMockServer.resetAll();
  }

  @TestTemplate
  @ExtendWith(TelecomSummaryInvocationContextProvider.class)
  public void unitTests(TestCase<Object, UserSyncStatusEntity, TelecomSummaryEntity, Object> testCase)
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
  protected void saveMains(List<TelecomSummaryEntity> accountSummaryEntities) {

    accountSummaryEntities
        .forEach(accountSummaryEntity -> mainRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, UserSyncStatusEntity, TelecomSummaryEntity, Object> testCase)
      throws ResponseNotOkException {

    mainService
        .listAccountSummaries(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateGParents(List<Object> expectedGParents) {
    
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
  protected void validateMains(List<TelecomSummaryEntity> expectedMains) {

    final List<TelecomSummaryEntity> actualMains = mainRepository.findAll();

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
