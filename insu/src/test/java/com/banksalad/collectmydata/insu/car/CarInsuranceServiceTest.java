package com.banksalad.collectmydata.insu.car;

import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceRequest;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.template.ServiceTest;
import com.banksalad.collectmydata.insu.template.provider.CarInsuranceInvocationContextProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@DisplayName("보험-004 자동차보험 정보 조회")
public class CarInsuranceServiceTest extends
    ServiceTest<Object, InsuranceSummaryEntity, CarInsuranceEntity, Object> {

  @Autowired
  private AccountInfoService<InsuranceSummary, GetCarInsuranceRequest, List<CarInsurance>> mainService;

  @Autowired
  private AccountInfoRequestHelper<GetCarInsuranceRequest, InsuranceSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<InsuranceSummary, List<CarInsurance>> responseHelper;

  @Autowired
  private InsuranceSummaryRepository parentRepository;

  @Autowired
  private CarInsuranceRepository mainRepository;

  @Autowired
  private CarInsuranceHistoryRepository historyRepository;

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

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
  @ExtendWith(CarInsuranceInvocationContextProvider.class)
  public void unitTests(TestCase<Object, InsuranceSummaryEntity, CarInsuranceEntity, Object> testCase) {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<InsuranceSummaryEntity> insuranceSummaryEntities) {

    /* updateBasicSearchTimestamp()에 의해서 testCase의 summaries가 오염되므로 복제본을 만들어야 한다. */
    insuranceSummaryEntities
        .forEach(insuranceSummaryEntity -> parentRepository.save(insuranceSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<CarInsuranceEntity> carInsuranceEntities) {

    carInsuranceEntities
        .forEach(depositAccountBasicEntity -> mainRepository.save(depositAccountBasicEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, InsuranceSummaryEntity, CarInsuranceEntity, Object> testCase) {

    mainService
        .listAccountInfos(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateParents(List<InsuranceSummaryEntity> expectedParents) {

    final List<InsuranceSummaryEntity> actualParents = parentRepository.findAll();

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
  protected void validateMains(List<CarInsuranceEntity> expectedMains) {

    final List<CarInsuranceEntity> actualMains = mainRepository.findAll();

    assertAll("*** Main 확인 ***",
        () -> assertEquals(expectedMains.size(), actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMains.size(); i++) {
            assertThat(actualMains.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedMains.get(i));
          }
        }
    );

    final List<CarInsuranceHistoryEntity> actualHistories = historyRepository.findAll();

    if (actualHistories.size() > 0) {
      assertAll("history 확인",
          () -> assertThat(actualMains.get(actualMains.size() - 1)).usingRecursiveComparison()
              .ignoringFields(IGNORING_ENTITY_FIELDS).isEqualTo(actualHistories.get(actualHistories.size() - 1))
      );
    }
  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
