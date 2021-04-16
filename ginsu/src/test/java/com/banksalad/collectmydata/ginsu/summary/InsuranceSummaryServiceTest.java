package com.banksalad.collectmydata.ginsu.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.ginsu.collect.Executions;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.ginsu.summary.dto.ListInsuranceSummariesRequest;
import com.banksalad.collectmydata.ginsu.template.ServiceTest;
import com.banksalad.collectmydata.ginsu.template.provider.InsuranceSummaryInvocationContextProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
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
@DisplayName("6.8 보증보험 목록 조회")
public class InsuranceSummaryServiceTest extends
    ServiceTest<Object, UserSyncStatusEntity, InsuranceSummaryEntity, Object> {

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @Autowired
  private SummaryService<ListInsuranceSummariesRequest, InsuranceSummary> summaryService;

  @Autowired
  private SummaryRequestHelper<ListInsuranceSummariesRequest> summaryRequestHelper;

  @Autowired
  private SummaryResponseHelper<InsuranceSummary> summaryResponseHelper;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  @Autowired
  private InsuranceSummaryRepository insuranceSummaryRepository;

  @BeforeAll
  public static void setUp() {
    wireMockServer.start();
  }

  @AfterEach
  public void tearDown() {
    wireMockServer.resetAll();
  }

  @AfterAll
  public static void tearDownAll() {
    wireMockServer.shutdown();
  }

  @TestTemplate
  @ExtendWith(InsuranceSummaryInvocationContextProvider.class)
  public void unitTests(TestCase<Object, UserSyncStatusEntity, InsuranceSummaryEntity, Object> testCase)
      throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    final Integer status = testCase.getExpectedResponses().get(testCase.getExpectedResponses().size() - 1).getStatus();

    if (status != null && status != 200) {
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
    userSyncStatusEntities.forEach(userSyncStatusRepository::save);
  }

  @Override
  protected void saveMains(List<InsuranceSummaryEntity> insuranceSummaryEntities) {
    insuranceSummaryEntities.forEach(insuranceSummaryRepository::save);
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, UserSyncStatusEntity, InsuranceSummaryEntity, Object> testCase)
      throws ResponseNotOkException {

    summaryService
        .listAccountSummaries(testCase.getExecutionContext(), Executions.finance_ginsu_summaries, summaryRequestHelper,
            summaryResponseHelper);
  }

  @Override
  protected void validateParents(List<UserSyncStatusEntity> expectedParents) {
    final List<UserSyncStatusEntity> actualParents = userSyncStatusRepository.findAll();

    assertAll("*** UserSyncStatusEntity 확인 ***",
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
  protected void validateMains(List<InsuranceSummaryEntity> expectedMains) {

    final List<InsuranceSummaryEntity> actualMains = insuranceSummaryRepository.findAll();

    assertAll("*** InsuranceSummaryEntity 확인 ***",
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
