package com.banksalad.collectmydata.invest.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.invest.account.dto.AccountProduct;
import com.banksalad.collectmydata.invest.account.dto.ListAccountProductsRequest;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductHistoryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.repository.AccountProductHistoryRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountProductRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import com.banksalad.collectmydata.invest.template.ServiceTest;
import com.banksalad.collectmydata.invest.template.provider.AccountProductInvocationContextProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
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
@DisplayName("6.4.4 계좌 상품정보 조회")
class AccountProductServiceTest extends ServiceTest<Object, AccountSummaryEntity, AccountProductEntity, Object> {

  @Autowired
  private AccountInfoService<AccountSummary, ListAccountProductsRequest, List<AccountProduct>> mainService;

  @Autowired
  private AccountInfoRequestHelper<ListAccountProductsRequest, AccountSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, List<AccountProduct>> responseHelper;

  @Autowired
  private AccountSummaryRepository parentRepository;

  @Autowired
  private AccountProductRepository mainRepository;

  @Autowired
  private AccountProductHistoryRepository mainHistoryRepository;

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeAll
  static void setup() {
    wireMockServer.start();
  }


  @AfterAll
  static void clean() {
    wireMockServer.shutdown();
  }

  @TestTemplate
  @ExtendWith(AccountProductInvocationContextProvider.class)
  void accountProductServiceTest(TestCase<Object, AccountSummaryEntity, AccountProductEntity, Object> testCase)
      throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<AccountSummaryEntity> accountSummaryEntities) {
    accountSummaryEntities
        .forEach(accountSummaryEntity -> parentRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<AccountProductEntity> accountProductEntities) {
    accountProductEntities
        .forEach(accountProductEntity -> mainRepository.save(accountProductEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, AccountSummaryEntity, AccountProductEntity, Object> testCase)
      throws ResponseNotOkException {

    mainService.listAccountInfos(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateParents(List<AccountSummaryEntity> expectedParents) {
    final List<AccountSummaryEntity> actualParents = parentRepository.findAll();

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
  protected void validateMains(List<AccountProductEntity> expectedMains) {
    final List<AccountProductEntity> actualMains = mainRepository.findAll();

    assertAll("*** AccountProductEntity 확인 ***",
        () -> assertEquals(expectedMains.size(), actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMains.size(); i++) {
            assertThat(actualMains.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedMains.get(i));
          }
        }
    );

    final List<AccountProductHistoryEntity> actualHistories = mainHistoryRepository.findAll();

    if (actualHistories.size() > 0) {
      assertAll("AccountProductHistoryEntity 확인",
          () -> assertThat(actualMains.get(actualMains.size() - 1)).usingRecursiveComparison()
              .ignoringFields(IGNORING_ENTITY_FIELDS).isEqualTo(actualHistories.get(actualHistories.size() - 1))
      );
    }
  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
