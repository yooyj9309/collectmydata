package com.banksalad.collectmydata.card.testcase;

import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.template.dto.BareMain;
import com.banksalad.collectmydata.card.template.dto.BareParent;
import com.banksalad.collectmydata.card.template.dto.BareResponse;
import com.banksalad.collectmydata.card.template.dto.TestCase;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;

import java.util.List;

import static com.banksalad.collectmydata.card.testcase.FinanceConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.NEW_USS_ST;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.OLD_USS_ST;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.RSP_CODE_SYSTEM_FAILURE;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.STATUS_INTERNAL_SERVER_ERROR;

/*
템플릿 메써드에 전달할 invocation context를 생성하기 위하여 모든 test case들을 여기에서 생성한다.
 */
public class CardSummaryTestCaseGenerator extends TestCaseGenerator {

  private static final Execution exeuciton = Executions.finance_card_summaries;
  private static final String rspCodeError = "50001";
  private static final String rspCodeOk = "00000";

  public static List<TestCase> get() {
    return List.of(
        TestCase.builder()
            .displayName("API 실패")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity1()))
            .summaryEntities(List.of(generateSummaryEntity1()))
            .execution(exeuciton)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_single_page_00").status(STATUS_INTERNAL_SERVER_ERROR)
                    .rspCode(RSP_CODE_SYSTEM_FAILURE).build()
            ))
            .build(),
        TestCase.builder()
            .displayName("기존 0건 + 2건 추가")
            .userSyncStatusEntities(null)
            .execution(exeuciton)
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
            .execution(exeuciton)
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
            .execution(exeuciton)
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
            .execution(exeuciton)
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
            .execution(exeuciton)
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
        .apiId(exeuciton.getApi().getId())
        .searchTimestamp(OLD_USS_ST)
        .build();
  }

  private static CardSummaryEntity generateSummaryEntity1() {
    return CardSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .cardId("card001")
        .cardNum("123456******456")
        .consent(true)
        .cardName("하나카드01")
        .cardMember(1)
        .build();
  }

}
