package com.banksalad.collectmydata.card.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

import com.banksalad.collectmydata.card.card.dto.ListPointsRequest;
import com.banksalad.collectmydata.card.card.dto.Point;
import com.banksalad.collectmydata.card.common.db.entity.PointEntity;
import com.banksalad.collectmydata.card.common.db.repository.PointHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.PointRepository;
import com.banksalad.collectmydata.card.template.ServiceTest;
import com.banksalad.collectmydata.card.template.provider.PointInvocationContextProvider;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@SpringBootTest
@Transactional
@DisplayName("카드-003 포인트 정보 조회")
public class PointServiceTest extends ServiceTest<Object, UserSyncStatusEntity, PointEntity, Object> {

  @Autowired
  private UserBaseService<ListPointsRequest, List<Point>> pointService;

  @Autowired
  private UserBaseRequestHelper<ListPointsRequest> requestHelper;

  @Autowired
  private UserBaseResponseHelper<List<Point>> responseHelper;

  @Autowired
  private PointRepository pointRepository;

  @Autowired
  private PointHistoryRepository pointHistoryRepository;

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
    wireMockServer.stop();
  }

  @TestTemplate
  @ExtendWith(PointInvocationContextProvider.class)
  public void unitTests(TestCase<Object, UserSyncStatusEntity, PointEntity, Object> testCase) throws ResponseNotOkException {

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
  protected void saveMains(List<PointEntity> pointEntities) {

  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, UserSyncStatusEntity, PointEntity, Object> testCase)
      throws ResponseNotOkException {
    throw new ResponseNotOkException(500, "50001", "responseMessage");

  }

  @Override
  protected void validateParents(List<UserSyncStatusEntity> expectedParents) {

  }

  @Override
  protected void validateMains(List<PointEntity> expectedMains) {

  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
