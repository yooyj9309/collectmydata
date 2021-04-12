package com.banksalad.collectmydata.capital.testcase;

import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.TestHelper;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.test.template.dto.BareMain;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

import java.util.List;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM2;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.capital.common.TestHelper.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEARCH_TIMESTAMP_100;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEARCH_TIMESTAMP_200;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO1;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO2;

public class CapitalSummaryTestCaseGenerator {

  private static final Execution exeuciton = Executions.capital_get_accounts;
  private static final String RSP_CODE_SYSTEM_FAILURE = "50001";
  private static final String rspCodeOk = "00000";

  public static List<TestCase> get() {
    return List.of(
//        TestCase.builder()
//            .displayName("API 실패")
//            .userSyncStatusEntities(null)
//            .execution(exeuciton)
//            .executionContext(TestHelper.getExecutionContext())
//            .errorOccurred(true)
//            .expectedExceptionClazz(ResponseNotOkException.class)
//            .expectedExceptionMessage("exception")
//            .expectedResponses(List.of(
//                BareResponse.builder().mockId("CP01_001_single_page_01")
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                    .rspCode(RSP_CODE_SYSTEM_FAILURE).build()
//            ))
//            .build(),
//        TestCase.builder()
//            .displayName("기존 0건 + 2건 추가")
//            .userSyncStatusEntities(null)
//            .execution(exeuciton)
//            .executionContext(TestHelper.getExecutionContext())
//            .expectedResponses(List.of(
//                BareResponse.builder().mockId("CP01_002_single_page_01").build()
//            ))
//            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
//            .expectedUserSyncStatusSearchTimestamp(SEARCH_TIMESTAMP_100)
//            .expectedMains(List.of(
//                BareMain.builder().syncedAt(NEW_SYNCED_AT).build(),
//                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
//            ))
//            .expectedMainEntities(List.of(
//                generateSummaryEntity1(),
//                generateSummaryEntity2()
//            ))
//            .build(),
//        TestCase.builder()
//            .displayName("기존 1건 + 0건 반환")
//            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity_withSearchTimestamp100()))
//            .summaryEntities(List.of(generateSummaryEntity1()))
//            .execution(exeuciton)
//            .executionContext(TestHelper.getExecutionContext())
//            .expectedResponses(List.of(
//                BareResponse.builder().mockId("CP01_003_single_page_01").build()
//            ))
//            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
//            .expectedUserSyncStatusSearchTimestamp(SEARCH_TIMESTAMP_200)
//            .expectedMains(List.of(
//                BareMain.builder().syncedAt(OLD_SYNCED_AT).build()
//            ))
//            .expectedMainEntities(List.of(
//                generateSummaryEntity1()
//            ))
//            .build(),
//        TestCase.builder()
//            .displayName("기존 1건 + 추가 1건")
//            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity_withSearchTimestamp100()))
//            .summaryEntities(List.of(generateSummaryEntity1()))
//            .execution(exeuciton)
//            .executionContext(TestHelper.getExecutionContext())
//            .expectedResponses(List.of(
//                BareResponse.builder().mockId("CP01_004_single_page_01").build()
//            ))
//            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
//            .expectedUserSyncStatusSearchTimestamp(SEARCH_TIMESTAMP_200)
//            .expectedMains(List.of(
//                BareMain.builder().syncedAt(OLD_SYNCED_AT).build(),
//                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
//            ))
//            .expectedMainEntities(List.of(
//                generateSummaryEntity1(),
//                generateSummaryEntity2()
//            ))
//            .build(),
//        TestCase.builder()
//            .displayName("기존 1건 + 동일 1건")
//            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity_withSearchTimestamp100()))
//            .summaryEntities(List.of(generateSummaryEntity1()))
//            .execution(exeuciton)
//            .executionContext(TestHelper.getExecutionContext())
//            .expectedResponses(List.of(
//                BareResponse.builder().mockId("CP01_005_single_page_01").build()
//            ))
//            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
//            .expectedUserSyncStatusSearchTimestamp(SEARCH_TIMESTAMP_200)
//            .expectedMains(List.of(
//                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
//            ))
//            .expectedMainEntities(List.of(
//                generateSummaryEntity1()
//            ))
//            .build(),
        TestCase.builder()
            .displayName("기존 1건 + 변경 1건")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity_withSearchTimestamp100()))
            .summaryEntities(List.of(generateSummaryEntity1()))
            .execution(exeuciton)
            .executionContext(TestHelper.getExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("CP01_006_single_page_01").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedUserSyncStatusSearchTimestamp(SEARCH_TIMESTAMP_200)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .expectedMainEntities(List.of(
                generateSummaryEntity1_update()
            ))
            .build()
    );
  }

  private static UserSyncStatusEntity generateUserSyncStatusEntity_withSearchTimestamp100() {
    return UserSyncStatusEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .apiId(exeuciton.getApi().getId())
        .searchTimestamp(SEARCH_TIMESTAMP_100)
        .build();
  }

  private static AccountSummaryEntity generateSummaryEntity1() {
    return AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .isConsent(true)
        .seqno(SEQNO1)
        .prodName("상품명1")
        .accountType("3100")
        .accountStatus("01")
        .build();
  }

  private static AccountSummaryEntity generateSummaryEntity2() {
    return AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM2)
        .isConsent(true)
        .seqno(SEQNO2)
        .prodName("상품명2")
        .accountType("3710")
        .accountStatus("03")
        .build();
  }

  private static AccountSummaryEntity generateSummaryEntity1_update() {
    return AccountSummaryEntity.builder()
        .syncedAt(NEW_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .isConsent(true)
        .seqno(SEQNO1)
        .prodName("(fix)상품명1")
        .accountType("3100")
        .accountStatus("01")
        .build();
  }
}
