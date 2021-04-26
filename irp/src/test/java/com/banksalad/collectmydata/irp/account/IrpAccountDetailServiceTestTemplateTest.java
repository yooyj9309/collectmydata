package com.banksalad.collectmydata.irp.account;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.irp.api.AccountInfoRequestPaginationHelper;
import com.banksalad.collectmydata.irp.api.AccountInfoResponsePaginationHelper;
import com.banksalad.collectmydata.irp.api.AccountInfoServicePagination;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailHistoryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.template.ServiceTest;
import com.banksalad.collectmydata.irp.template.provider.IrpAccountDetailInvocationContextProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.RequiredArgsConstructor;
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

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@RequiredArgsConstructor
@DisplayName("6.1.5 개인형 IRP 계좌 추가정보 조회")
class IrpAccountDetailServiceTestTemplateTest extends
    ServiceTest<Object, IrpAccountSummaryEntity, IrpAccountDetailEntity, Object> {

  private final AccountInfoServicePagination<IrpAccountSummary, IrpAccountDetailRequest, List<IrpAccountDetail>> mainService;

  private final AccountInfoRequestPaginationHelper<IrpAccountDetailRequest, IrpAccountSummary> requestHelper;

  private final AccountInfoResponsePaginationHelper<IrpAccountDetailRequest, IrpAccountSummary, List<IrpAccountDetail>> responseHelper;

  private final IrpAccountSummaryRepository accountSummaryRepository;

  private final IrpAccountDetailRepository accountDetailRepository;

  private final IrpAccountDetailHistoryRepository irpAccountDetailHistoryRepository;

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
  @ExtendWith(IrpAccountDetailInvocationContextProvider.class)
  void accountDetailServiceTest(TestCase<Object, IrpAccountSummaryEntity, IrpAccountDetailEntity, Object> testCase) {

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
  protected void saveMains(List<IrpAccountDetailEntity> accountDetailEntities) {

    accountDetailEntities
        .forEach(irpAccountDetailEntity -> accountDetailRepository.save(irpAccountDetailEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, IrpAccountSummaryEntity, IrpAccountDetailEntity, Object> testCase) {

    mainService
        .listAccountInfos(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateGParents(List<Object> expectedGParents) {

  }

  @Override
  protected void validateParents(List<IrpAccountSummaryEntity> expectedParents) {

    final List<IrpAccountSummaryEntity> actualParents = accountSummaryRepository.findAll();

    assertAll("*** IrpAccountSummaryEntity 확인 ***",
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
  protected void validateMains(List<IrpAccountDetailEntity> expectedMains) {

    final List<IrpAccountDetailEntity> actualMains = accountDetailRepository.findAll();

    assertAll("*** IrpAccountDetailEntity 확인 ***",
        () -> assertEquals(expectedMains.size(), actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMains.size(); i++) {
            assertThat(actualMains.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedMains.get(i));
          }
        }
    );

    final List<IrpAccountDetailHistoryEntity> actualHistories = irpAccountDetailHistoryRepository.findAll();

    if (actualHistories.size() > 0) {
      assertAll("IrpAccountDetailHistoryEntity 확인",
          () -> assertThat(actualMains.get(actualMains.size() - 1)).usingRecursiveComparison()
              .ignoringFields(IGNORING_ENTITY_FIELDS).isEqualTo(actualHistories.get(actualHistories.size() - 1))
      );
    }
  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
