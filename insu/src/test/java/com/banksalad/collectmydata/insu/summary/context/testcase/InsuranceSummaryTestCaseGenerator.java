package com.banksalad.collectmydata.insu.summary.context.testcase;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.template.TestCaseGenerator;
import com.banksalad.collectmydata.insu.common.template.dto.BareMain;
import com.banksalad.collectmydata.insu.common.template.dto.BareResponse;
import com.banksalad.collectmydata.insu.common.template.dto.TestCase;

import java.util.List;

import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.NEW_USS_ST;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.OLD_USS_ST;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.RSP_CODE_SYSTEM_FAILURE;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.STATUS_INTERNAL_SERVER_ERROR;

/*
템플릿 메써드에 전달할 invocation context를 생성하기 위하여 모든 test case들을 여기에서 생성한다.
 */
public class InsuranceSummaryTestCaseGenerator extends TestCaseGenerator {

  private static final Execution execution = Executions.insurance_get_summaries;

  public static List<TestCase> get() {
    return List.of(
        TestCase.builder()
            .displayName("API 실패")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity1()))
            .summaryEntities(List.of(generateSummaryEntity1()))
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_single_page_00").status(STATUS_INTERNAL_SERVER_ERROR)
                    .rspCode(RSP_CODE_SYSTEM_FAILURE).build()
            ))
            .build()
        , TestCase.builder()
            .displayName("기존 0건 + 2건 추가")
            .userSyncStatusEntities(null)
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("002_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedUserSyncStatusSearchTimestamp(OLD_USS_ST)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build(),
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .build(),
        TestCase.builder()
            .displayName("기존 1건 + 0건 반환")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity1()))
            .summaryEntities(List.of(generateSummaryEntity1()))
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("003_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedUserSyncStatusSearchTimestamp(OLD_USS_ST)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(OLD_SYNCED_AT).build()
            ))
            .build(),
        TestCase.builder()
            .displayName("기존 1건 + 추가 1건")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity1()))
            .summaryEntities(List.of(generateSummaryEntity1()))
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("004_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedUserSyncStatusSearchTimestamp(NEW_USS_ST)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(OLD_SYNCED_AT).build(),
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .build(),
        TestCase.builder()
            .displayName("기존 1건 + 동일 1건")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity1()))
            .summaryEntities(List.of(generateSummaryEntity1()))
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("005_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedUserSyncStatusSearchTimestamp(OLD_USS_ST)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .build(),
        TestCase.builder()
            .displayName("기존 1건 + 변경 1건")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity1()))
            .summaryEntities(List.of(generateSummaryEntity1()))
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("006_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedUserSyncStatusSearchTimestamp(NEW_USS_ST)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .build()
    );
  }

  private static UserSyncStatusEntity generateUserSyncStatusEntity1() {
    return UserSyncStatusEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .apiId(execution.getApi().getId())
        .searchTimestamp(OLD_USS_ST)
        .build();
  }

  private static InsuranceSummaryEntity generateSummaryEntity1() {
    return InsuranceSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("123456789")
        .consent(true)
        .insuType("05")
        .prodName("묻지도 따지지도않고 암보험")
        .insuStatus("01")
        .build();
  }
}
