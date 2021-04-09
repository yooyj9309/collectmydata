package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.context.provider.AccountDetailInvocationContextProvider;
import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.account.dto.GetAccountDetailRequest;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.mapper.AccountDetailHistoryMapper;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectmydataRuntimeException;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.test.template.dto.BareMain;
import com.banksalad.collectmydata.finance.test.template.dto.BareParent;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.capital.common.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.capital.common.constant.CapitalConstants.ACCOUNT_DETAIL_URL_REGEX;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@DisplayName("할부금융-003 대출상품계좌 추가정보 조회")
public class AccountDetailServiceTest {

  @Autowired
  private AccountInfoService<AccountSummary, GetAccountDetailRequest, AccountDetail> service;

  @Autowired
  private AccountDetailRequestHelper requestHelper;

  @Autowired
  private AccountDetailResponseHelper responseHelper;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  @Autowired
  private AccountSummaryRepository parentRepository;

  @Autowired
  private AccountDetailRepository repository;

  @Autowired
  private AccountDetailHistoryRepository historyRepository;

  private AccountDetailHistoryMapper historyMapper = Mappers.getMapper(AccountDetailHistoryMapper.class);

  private static WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  private ExecutionContext executionContext;

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

  /*
  컴포지션을 이용한 템플릿 패턴을 적용한다.
  이 파일은 summary 테스트를 위한 템플릿 메써드이므로 수정하지 않는다.
  대신 testcase.DetailTestCase 만을 수정한다.
   */
  @TestTemplate
  @ExtendWith(AccountDetailInvocationContextProvider.class)
  public void unitTests(TestCase testCase) {

    /* Given */
    prepareRepositories(testCase);

    final Execution execution = testCase.getExecution();
    final String apiId = execution.getApi().getId();
    ExecutionContext executionContext = testCase.getExecutionContext();
    final long bankSaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final int expectedParentSize = (testCase.getExpectedParents() == null) ? 0 : testCase.getExpectedParents().size();
    final int expectedMainSize = (testCase.getExpectedMains() == null) ? 0 : testCase.getExpectedMains().size();
    final String organizationHost = "http://" + executionContext.getOrganizationHost() + ":" + wireMockServer.port();

    executionContext.setOrganizationHost(organizationHost);
    stubMockServer(apiId, testCase.getExpectedResponses());

    /* When */
    service.listAccountInfos(executionContext, execution, requestHelper, responseHelper);

    /* Then */
    final UserSyncStatusEntity actualUserSyncStatus = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(bankSaladUserId, organizationId, apiId)
        .orElseThrow(() -> new CollectmydataRuntimeException("No record found"));

    assertAll("userSyncStatus 확인",
        () -> verifyEquals(testCase.getExpectedUserSyncStatusSyncedAt(), actualUserSyncStatus.getSyncedAt())
    );

    final List<AccountSummaryEntity> actualParents = parentRepository.findAll();
    assertAll("parent 확인",
        () -> assertEquals(expectedParentSize, actualParents.size()),
        () -> {
          for (int i = 0; i < expectedParentSize; i++) {
            final BareParent expectedParent = testCase.getExpectedParents().get(i);
            final AccountSummaryEntity actualParent = actualParents.get(i);

            verifyEquals(expectedParent.getSyncedAt(), actualParent.getSyncedAt());
            assertEquals(expectedParent.getTransactionAt(), actualParent.getTransactionSyncedAt());
            assertEquals(expectedParent.getResponseCode(), actualParent.getTransactionResponseCode());
          }
        }
    );

    final List<AccountDetailEntity> actualMains = repository.findAll();
    assertAll("main 확인",
        () -> assertEquals(expectedMainSize, actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMainSize; i++) {
            final BareMain expectedMain = testCase.getExpectedMains().get(i);
            final AccountDetailEntity actualMain = actualMains.get(i);

            verifyEquals(expectedMain.getSyncedAt(), actualMain.getSyncedAt());
            if (testCase.getExpectedMainEntities() != null) {
              final AccountDetailEntity expectedMainEntity = (AccountDetailEntity) testCase.getExpectedMainEntities()
                  .get(i);

              assertThat(actualMain).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
                  .isEqualTo(expectedMainEntity);
            }
          }
        }
    );

    final List<AccountDetailHistoryEntity> actualHistoryEntities = historyRepository.findAll();
    if (actualHistoryEntities.size() > 0) {
      assertAll("history 확인",
          () -> verifyEquals(actualMains.get(actualMains.size() - 1),
              actualHistoryEntities.get(actualHistoryEntities.size() - 1))
      );
    }
  }

  private void prepareRepositories(TestCase testCase) {

    if (testCase.getUserSyncStatusEntities() != null) {
      userSyncStatusRepository.saveAll(testCase.getUserSyncStatusEntities());
    }
    if (testCase.getParentEntities() != null) {
      testCase.getParentEntities().stream().flatMap(Stream::ofNullable)
          .forEach(o -> parentRepository.save((AccountSummaryEntity) o));
    }
    if (testCase.getMainEntities() != null) {
      testCase.getMainEntities().stream().flatMap(Stream::ofNullable)
          .forEach(o -> {
            repository.save((AccountDetailEntity) o);
            historyRepository.save(historyMapper.toHistoryEntity((AccountDetailEntity) o));
          });
    }
  }

  private void stubMockServer(String apiId, List<BareResponse> expectedResponses) {

    for (final BareResponse response : expectedResponses) {
      final String fileName = apiId + "_" + response.getMockId() + ".json";
      final int status = (response.getStatus() == null) ? 200 : response.getStatus();

      wireMockServer.stubFor(post(urlMatching(ACCOUNT_DETAIL_URL_REGEX))
          .withRequestBody(
              equalToJson(readText("classpath:mock/request/" + fileName)))
          .willReturn(
              aResponse()
                  .withStatus(status)
                  .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                  .withBody(readText("classpath:mock/response/" + fileName))));
    }
  }

  private void verifyEquals(LocalDateTime expected, LocalDateTime actual) {

    // OS에 따라 DB가 microsecond 단위에서 반올림 발생하여 보정한다.
    assertThat(actual).isCloseTo(expected, within(1, ChronoUnit.MICROS));
  }

  private void verifyEquals(AccountDetailEntity expected, AccountDetailHistoryEntity actual) {
    assertThat(actual).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD).isEqualTo(expected);
  }
}
