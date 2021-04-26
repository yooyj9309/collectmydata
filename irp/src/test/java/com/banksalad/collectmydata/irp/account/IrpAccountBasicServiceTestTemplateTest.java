package com.banksalad.collectmydata.irp.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicHistoryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasic;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.template.ServiceTest;
import com.banksalad.collectmydata.irp.template.provider.IrpAccountBasicInvocationContextProvider;
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

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("6.4.2 계좌 기본정보 조회 테스트")
class IrpAccountBasicServiceTestTemplateTest extends
    ServiceTest<Object, IrpAccountSummaryEntity, IrpAccountBasicEntity, Object> {

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @Autowired
  private AccountInfoService<IrpAccountSummary, IrpAccountBasicRequest, IrpAccountBasic> service;

  @Autowired
  private AccountInfoRequestHelper<IrpAccountBasicRequest, IrpAccountSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<IrpAccountSummary, IrpAccountBasic> responseHelper;

  @Autowired
  private IrpAccountSummaryRepository accountSummaryRepository;

  @Autowired
  private IrpAccountBasicRepository accountBasicRepository;

  @Autowired
  private IrpAccountBasicHistoryRepository accountBasicHistoryRepository;

  @BeforeAll
  static void setup() {
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
  @ExtendWith(IrpAccountBasicInvocationContextProvider.class)
  void unitTests(TestCase<Object, IrpAccountSummaryEntity, IrpAccountBasicEntity, Object> testCase)
      throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<IrpAccountSummaryEntity> accountSummaryEntities) {

    /* updateBasicSearchTimestamp()에 의해서 testCase의 summaries가 오염되므로 복제본을 만들어야 한다. */
    accountSummaryEntities
        .forEach(accountSummaryEntity -> accountSummaryRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<IrpAccountBasicEntity> accountBasicEntities) {

    accountBasicEntities
        .forEach(accountBasicEntity -> accountBasicRepository.save(accountBasicEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, IrpAccountSummaryEntity, IrpAccountBasicEntity, Object> testCase)
      throws ResponseNotOkException {

    service
        .listAccountInfos(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateGParents(List<Object> expectedGParents) {
    
  }

  @Override
  protected void validateParents(List<IrpAccountSummaryEntity> expectedParents) {
    final List<IrpAccountSummaryEntity> actualParents = accountSummaryRepository.findAll();

    assertAll("*** AccountSummaryEntity 확인 ***",
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
  protected void validateMains(List<IrpAccountBasicEntity> expectedMains) {
    final List<IrpAccountBasicEntity> actualMains = accountBasicRepository.findAll();

    assertAll("*** AccountBasicEntity 확인 ***",
        () -> assertEquals(expectedMains.size(), actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMains.size(); i++) {
            assertThat(actualMains.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedMains.get(i));
          }
        }
    );

    final List<IrpAccountBasicHistoryEntity> actualHistories = accountBasicHistoryRepository.findAll();

    if (actualHistories.size() > 0) {
      assertAll("AccountBasicHistoryEntity 확인",
          () -> assertThat(actualMains.get(actualMains.size() - 1)).usingRecursiveComparison()
              .ignoringFields(IGNORING_ENTITY_FIELDS).isEqualTo(actualHistories.get(actualHistories.size() - 1))
      );
    }
  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
