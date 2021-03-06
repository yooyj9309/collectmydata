package com.banksalad.collectmydata.telecom.telecom;

import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomBillEntity;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomBillHistoryEntity;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomBillHistoryRepository;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomBillRepository;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomBillsRequest;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBill;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBillRequestSupporter;
import com.banksalad.collectmydata.telecom.template.ServiceTest;
import com.banksalad.collectmydata.telecom.template.provider.TelecomBillInvocationContextProvider;

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
@DisplayName("??????-002 ?????? ?????? ??????")
public class TelecomBillServiceTest extends
    ServiceTest<Object, UserSyncStatusEntity, TelecomBillEntity, Object> {

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired
  private AccountInfoService<TelecomBillRequestSupporter, ListTelecomBillsRequest, List<TelecomBill>> mainService;
  @Autowired
  private AccountInfoRequestHelper<ListTelecomBillsRequest, TelecomBillRequestSupporter> requestHelper;
  @Autowired
  private AccountInfoResponseHelper<TelecomBillRequestSupporter, List<TelecomBill>> responseHelper;
  @Autowired
  private UserSyncStatusRepository parentRepository;
  @Autowired
  private TelecomBillRepository mainRepository;
  @Autowired
  private TelecomBillHistoryRepository historyRepository;

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
  @ExtendWith(TelecomBillInvocationContextProvider.class)
  public void unitTests(TestCase<Object, UserSyncStatusEntity, TelecomBillEntity, Object> testCase) {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<UserSyncStatusEntity> userSyncStatusEntities) {

    /* updateBasicSearchTimestamp()??? ????????? testCase??? summaries??? ??????????????? ???????????? ???????????? ??????. */
    userSyncStatusEntities
        .forEach(accountSummaryEntity -> parentRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<TelecomBillEntity> depositAccountBasicEntities) {

    depositAccountBasicEntities
        .forEach(depositAccountBasicEntity -> mainRepository.save(depositAccountBasicEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, UserSyncStatusEntity, TelecomBillEntity, Object> testCase) {

    mainService
        .listAccountInfos(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateGParents(List<Object> expectedGParents) {
    
  }

  @Override
  protected void validateParents(List<UserSyncStatusEntity> expectedParents) {

    final List<UserSyncStatusEntity> actualParents = parentRepository.findAll();

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
  protected void validateMains(List<TelecomBillEntity> expectedMains) {

    final List<TelecomBillEntity> actualMains = mainRepository.findAll();

    assertAll("*** Main ?????? ***",
        () -> assertEquals(expectedMains.size(), actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMains.size(); i++) {
            assertThat(actualMains.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedMains.get(i));
          }
        }
    );

    final List<TelecomBillHistoryEntity> actualHistories = historyRepository.findAll();

    if (actualHistories.size() > 0) {
      assertAll("history ??????",
          () -> assertThat(actualMains.get(actualMains.size() - 1)).usingRecursiveComparison()
              .ignoringFields(IGNORING_ENTITY_FIELDS).isEqualTo(actualHistories.get(actualHistories.size() - 1))
      );
    }
  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
