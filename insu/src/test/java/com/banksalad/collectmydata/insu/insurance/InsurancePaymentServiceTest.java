package com.banksalad.collectmydata.insu.insurance;

import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsurancePaymentRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsurancePaymentRequest;
import com.banksalad.collectmydata.insu.insurance.dto.InsurancePayment;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

import com.banksalad.collectmydata.insu.template.ServiceTest;
import com.banksalad.collectmydata.insu.template.provider.InsurancePaymentInvocationContextProvider;
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
@DisplayName("보험-005 보험 납입정보 조회")
class InsurancePaymentServiceTest extends ServiceTest<Object, InsuranceSummaryEntity, InsurancePaymentEntity, Object> {

  @Autowired
  private AccountInfoService<InsuranceSummary, GetInsurancePaymentRequest, InsurancePayment> mainService;
  @Autowired
  private AccountInfoRequestHelper<GetInsurancePaymentRequest, InsuranceSummary> requestHelper;
  @Autowired
  private AccountInfoResponseHelper<InsuranceSummary, InsurancePayment> responseHelper;
  @Autowired
  private InsuranceSummaryRepository parentRepository;
  @Autowired
  private InsurancePaymentRepository mainRepository;

  public static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

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
  @ExtendWith(InsurancePaymentInvocationContextProvider.class)
  public void unitTests(TestCase<Object, InsuranceSummaryEntity, InsurancePaymentEntity, Object> testCase) {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<InsuranceSummaryEntity> insuranceSummaryEntities) {

    /* DB save()에 의해서 testCase의 summaries가 오염되므로 복제본을 만들어야 한다. */
    insuranceSummaryEntities
        .forEach(insuranceSummaryEntity -> parentRepository.save(insuranceSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<InsurancePaymentEntity> insurancePaymentEntities) {

    insurancePaymentEntities
        .forEach(insurancePaymentEntity -> mainRepository.save(insurancePaymentEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(
      TestCase<Object, InsuranceSummaryEntity, InsurancePaymentEntity, Object> testCase) {

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
  protected void validateMains(List<InsurancePaymentEntity> expectedMains) {

    final List<InsurancePaymentEntity> actualMains = mainRepository.findAll();

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
