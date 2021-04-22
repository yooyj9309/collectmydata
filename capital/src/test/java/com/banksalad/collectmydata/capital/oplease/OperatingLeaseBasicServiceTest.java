package com.banksalad.collectmydata.capital.oplease;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseBasicEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseBasicHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseBasicHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseBasicRepository;
import com.banksalad.collectmydata.capital.oplease.dto.GetOperatingLeaseBasicRequest;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasic;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.capital.template.ServiceTest;
import com.banksalad.collectmydata.capital.template.provider.OperatingLeaseBasicInvocationContextProvider;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
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
@DisplayName("할부금융-005 운용리스 기본정보 조회")
public class OperatingLeaseBasicServiceTest extends
    ServiceTest<Object, AccountSummaryEntity, OperatingLeaseBasicEntity, Object> {

  @Autowired
  private AccountInfoService<AccountSummary, GetOperatingLeaseBasicRequest, OperatingLeaseBasic> mainService;

  @Autowired
  private AccountInfoRequestHelper<GetOperatingLeaseBasicRequest, AccountSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, OperatingLeaseBasic> responseHelper;

  @Autowired
  private AccountSummaryRepository parentRepository;

  @Autowired
  private OperatingLeaseBasicRepository mainRepository;

  @Autowired
  private OperatingLeaseBasicHistoryRepository historyRepository;

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
  @ExtendWith(OperatingLeaseBasicInvocationContextProvider.class)
  public void unitTests(TestCase<Object, AccountSummaryEntity, OperatingLeaseBasicEntity, Object> testCase) {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<AccountSummaryEntity> accountSummaryEntities) {

    /* updateBasicSearchTimestamp()에 의해서 testCase의 summaries가 오염되므로 복제본을 만들어야 한다. */
    accountSummaryEntities
        .forEach(accountSummaryEntity -> parentRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<OperatingLeaseBasicEntity> OperatingLeaseEntities) {

    OperatingLeaseEntities
        .forEach(OperatingLeaseEntity -> mainRepository.save(OperatingLeaseEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, AccountSummaryEntity, OperatingLeaseBasicEntity, Object> testCase) {

    mainService
        .listAccountInfos(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateParents(List<AccountSummaryEntity> expectedParents) {

    final List<AccountSummaryEntity> actualParents = parentRepository.findAll();

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
  protected void validateMains(List<OperatingLeaseBasicEntity> expectedMains) {

    final List<OperatingLeaseBasicEntity> actualMains = mainRepository.findAll();

    assertAll("*** Main 확인 ***",
        () -> assertEquals(expectedMains.size(), actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMains.size(); i++) {
            assertThat(actualMains.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedMains.get(i));
          }
        }
    );

    final List<OperatingLeaseBasicHistoryEntity> actualHistories = historyRepository.findAll();

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
