package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.dto.ListRevolvingsRequest;
import com.banksalad.collectmydata.card.card.dto.Revolving;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.PointEntity;
import com.banksalad.collectmydata.card.common.db.entity.RevolvingEntity;
import com.banksalad.collectmydata.card.common.db.entity.RevolvingHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.RevolvingHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.RevolvingRepository;
import com.banksalad.collectmydata.card.template.ServiceTest;
import com.banksalad.collectmydata.card.template.provider.PointInvocationContextProvider;
import com.banksalad.collectmydata.card.template.provider.RevolvingInvocationContextProvider;
import com.banksalad.collectmydata.card.util.TestHelper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.banksalad.collectmydata.card.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@DisplayName("카드-010 리볼빙 정보 조회")
public class RevolvingServiceTest extends ServiceTest<Object, UserSyncStatusEntity, RevolvingEntity, Object> {

  @Autowired
  private UserBaseService<ListRevolvingsRequest, List<Revolving>> revolvingService;

  @Autowired
  private UserBaseRequestHelper<ListRevolvingsRequest> requestHelper;

  @Autowired
  private UserBaseResponseHelper<List<Revolving>> responseHelper;

  @Autowired
  private RevolvingRepository revolvingRepository;

  @Autowired
  private RevolvingHistoryRepository revolvingHistoryRepository;

  private static WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeAll
  static void setup() {
    wireMockServer.start();
  }

  @AfterEach
  void tearDown() {
    wireMockServer.resetAll();
  }

  @AfterAll
  static void shutDown() {
    wireMockServer.shutdown();
  }

  @TestTemplate
  @ExtendWith(RevolvingInvocationContextProvider.class)
  public void unitTests(TestCase<Object, UserSyncStatusEntity, RevolvingEntity, Object> testCase) throws ResponseNotOkException {

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

  }

  @Override
  protected void saveMains(List<RevolvingEntity> revolvingEntities) {

  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, UserSyncStatusEntity, RevolvingEntity, Object> testCase)
      throws ResponseNotOkException {
    throw new ResponseNotOkException(500, "50001", "responseMessage");


  }

  @Override
  protected void validateParents(List<UserSyncStatusEntity> expectedParents) {

  }

  @Override
  protected void validateMains(List<RevolvingEntity> expectedMains) {

  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
