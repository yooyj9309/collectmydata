package com.banksalad.collectmydata.insu.car;

import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceRepository;
import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceTransactionRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.template.ServiceTest;
import com.banksalad.collectmydata.insu.template.provider.CarInsuranceTransactionInvocationContextProvider;

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
@DisplayName("보험-007 자동차보험 거래내역 조회")
public class CarInsuranceTransactionServiceTest extends
    ServiceTest<InsuranceSummaryEntity, CarInsuranceEntity, CarInsuranceTransactionEntity, Object> {

  @Autowired
  private TransactionApiService<CarInsurance, ListCarInsuranceTransactionsRequest, CarInsuranceTransaction> mainService;

  @Autowired
  private CarInsuranceTransactionRequestHelper requestHelper;

  @Autowired
  private CarInsuranceTransactionResponseHelper responseHelper;

  @Autowired
  private InsuranceSummaryRepository gParentRepository;

  @Autowired
  private CarInsuranceRepository parentRepository;

  @Autowired
  private CarInsuranceTransactionRepository mainRepository;

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
  @ExtendWith(CarInsuranceTransactionInvocationContextProvider.class)
  public void unitTests(
      TestCase<InsuranceSummaryEntity, CarInsuranceEntity, CarInsuranceTransactionEntity, Object> testCase) {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<InsuranceSummaryEntity> insuranceSummaryEntities) {

    insuranceSummaryEntities
        .forEach(insuranceSummaryEntity -> gParentRepository.save(insuranceSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveParents(List<CarInsuranceEntity> accountSummaryEntities) {

    /* DB save()에 의해서 testCase의 summaries가 오염되므로 복제본을 만들어야 한다. */
    accountSummaryEntities
        .forEach(accountSummaryEntity -> parentRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<CarInsuranceTransactionEntity> depositAccountBasicEntities) {

    depositAccountBasicEntities
        .forEach(depositAccountBasicEntity -> mainRepository.save(depositAccountBasicEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(
      TestCase<InsuranceSummaryEntity, CarInsuranceEntity, CarInsuranceTransactionEntity, Object> testCase) {

    mainService
        .listTransactions(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateParents(List<CarInsuranceEntity> expectedParents) {

    final List<CarInsuranceEntity> actualParents = parentRepository.findAll();

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
  protected void validateMains(List<CarInsuranceTransactionEntity> expectedMains) {

    final List<CarInsuranceTransactionEntity> actualMains = mainRepository.findAll();

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
