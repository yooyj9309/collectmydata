package com.banksalad.collectmydata.ginsu.insurance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicHistoryEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredHistoryEntity;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuranceBasicHistoryRepository;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuranceBasicRepository;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuredHistoryRepository;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuredRepository;
import com.banksalad.collectmydata.ginsu.insurance.dto.GetInsuranceBasicRequest;
import com.banksalad.collectmydata.ginsu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.ginsu.template.ServiceTest;
import com.banksalad.collectmydata.ginsu.template.provider.InsuranceBasicInvocationContextProvider;
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
@DisplayName("6.8.2 보증보험 기본정보 조회")
class InsuranceBasicInfoServiceTest extends ServiceTest<Object, InsuranceSummaryEntity, InsuranceBasicEntity, InsuredEntity> {

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @Autowired
  private AccountInfoService<InsuranceSummary, GetInsuranceBasicRequest, InsuranceBasic> insuranceBasicInfoService;

  @Autowired
  private AccountInfoRequestHelper<GetInsuranceBasicRequest, InsuranceSummary> insuranceBasicInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<InsuranceSummary, InsuranceBasic> insuranceBasicInfoResponseHelper;

  @Autowired
  private InsuranceSummaryRepository insuranceSummaryRepository;

  @Autowired
  private InsuranceBasicRepository insuranceBasicRepository;

  @Autowired
  private InsuranceBasicHistoryRepository insuranceBasicHistoryRepository;

  @Autowired
  private InsuredRepository insuredRepository;

  @Autowired
  private InsuredHistoryRepository insuredHistoryRepository;

  @BeforeAll
  static void setUp() {
    wireMockServer.start();
  }

  @AfterEach
  void tearDown() {
    wireMockServer.resetAll();
  }

  @AfterAll
  static void tearDownAll() {
    wireMockServer.shutdown();
  }

  @TestTemplate
  @ExtendWith(InsuranceBasicInvocationContextProvider.class)
  void unitTests(TestCase<Object, InsuranceSummaryEntity, InsuranceBasicEntity, InsuredEntity> testCase)
      throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<InsuranceSummaryEntity> insuranceSummaryEntities) {
    insuranceSummaryEntities
        .forEach(insuranceSummaryEntity -> insuranceSummaryRepository.save(insuranceSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<InsuranceBasicEntity> insuranceBasicEntities) {
    insuranceBasicEntities
        .forEach(insuranceBasicEntity -> insuranceBasicRepository.save(insuranceBasicEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<InsuredEntity> insuredEntities) {
    insuredEntities.forEach(insuredEntity -> insuredRepository.save(insuredEntity.toBuilder().build()));
  }

  @Override
  protected void runMainService(TestCase<Object, InsuranceSummaryEntity, InsuranceBasicEntity, InsuredEntity> testCase)
      throws ResponseNotOkException {

    insuranceBasicInfoService
        .listAccountInfos(testCase.getExecutionContext(), testCase.getExecution(), insuranceBasicInfoRequestHelper,
            insuranceBasicInfoResponseHelper);
  }

  @Override
  protected void validateParents(List<InsuranceSummaryEntity> expectedParents) {
    final List<InsuranceSummaryEntity> actualParents = insuranceSummaryRepository.findAll();

    assertAll("*** InsuranceSummaryEntity 확인 ***",
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
  protected void validateMains(List<InsuranceBasicEntity> expectedMains) {
    final List<InsuranceBasicEntity> actualMains = insuranceBasicRepository.findAll();

    assertAll("*** InsuranceBasicEntity 확인 ***",
        () -> assertEquals(expectedMains.size(), actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMains.size(); i++) {
            assertThat(actualMains.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedMains.get(i));
          }
        }
    );

    final List<InsuranceBasicHistoryEntity> actualHistories = insuranceBasicHistoryRepository.findAll();

    if (actualHistories.size() > 0) {
      assertAll("InsuranceBasicHistoryEntity 확인",
          () -> assertThat(actualMains.get(actualMains.size() - 1)).usingRecursiveComparison()
              .ignoringFields(IGNORING_ENTITY_FIELDS).isEqualTo(actualHistories.get(actualHistories.size() - 1))
      );
    }
  }

  @Override
  protected void validateChildren(List<InsuredEntity> expectedChildren) {
    final List<InsuredEntity> actualChildren = insuredRepository.findAll();

    assertAll("*** InsuredEntity 확인 ***",
        () -> assertEquals(expectedChildren.size(), actualChildren.size()),
        () -> {
          for (int i = 0; i < expectedChildren.size(); i++) {
            assertThat(actualChildren.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedChildren.get(i));
          }
        }
    );

    final List<InsuredHistoryEntity> actualHistories = insuredHistoryRepository.findAll();

    if (actualHistories.size() > 0) {
      assertAll("InsuredHistoryEntity 확인",
          () -> assertThat(actualChildren.get(actualChildren.size() - 1)).usingRecursiveComparison()
              .ignoringFields(IGNORING_ENTITY_FIELDS).isEqualTo(actualHistories.get(actualHistories.size() - 1))
      );
    }
  }
}
