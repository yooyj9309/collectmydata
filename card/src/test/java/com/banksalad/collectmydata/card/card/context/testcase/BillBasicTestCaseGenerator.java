package com.banksalad.collectmydata.card.card.context.testcase;

import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.BillEntity;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.test.template.TestCaseGenerator;
import com.banksalad.collectmydata.finance.test.template.dto.BareMain;
import com.banksalad.collectmydata.finance.test.template.dto.BareRequest;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_FORBIDDEN;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_TOO_MANY_REQUEST;

/*
템플릿 메써드에 전달할 invocation context를 생성하기 위하여 모든 test case들을 여기에서 생성한다.
 */
public class BillBasicTestCaseGenerator extends TestCaseGenerator {

  private static final Execution exeuciton = Executions.finance_card_bills;

  public static List<TestCase> get() {

    return List.of(
        TestCase.builder()
            .displayName("너무 많은 요청")
            .userSyncStatusEntities(generateUserSyncStatusEntities())
            .execution(exeuciton)
            .executionContext(generateExecutionContext())
            .requestParams(List.of(
                BareRequest.builder().nextPage(null).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_single_page_00").status(STATUS_TOO_MANY_REQUEST)
                    .rspCode(RSP_CODE_OVER_QUOTA).build()
            ))
            .expectedUserSyncStatusSyncedAt(OLD_SYNCED_AT)
            .build()
        ,
        TestCase.builder()
            .displayName("기존 0건 + 추가 2건 + 실패 1건")
            .userSyncStatusEntities(generateUserSyncStatusEntities())
            .execution(exeuciton)
            .executionContext(generateExecutionContext())
            .requestParams(List.of(
                BareRequest.builder().nextPage(null).build(),
                BareRequest.builder().nextPage("002").build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("002_multi_page_00").build(),
                BareResponse.builder().mockId("002_multi_page_01").status(STATUS_FORBIDDEN).build()
            ))
            .expectedUserSyncStatusSyncedAt(OLD_SYNCED_AT)
            // TODO: 부모가 user_sync_status인 셈이라 rspCode를 저장할 공간이 없음
            .expectedMains(List.of(
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build(),
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .build()
        ,
        TestCase.builder()
            .displayName("기존 0건 + 추가 3건")
            .userSyncStatusEntities(generateUserSyncStatusEntities())
            .execution(exeuciton)
            .executionContext(generateExecutionContext())
            .requestParams(List.of(
                BareRequest.builder().nextPage(null).build(),
                BareRequest.builder().nextPage("002").build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("003_multi_page_00").build(),
                BareResponse.builder().mockId("003_multi_page_01").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build(),
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build(),
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .build()
        ,
        TestCase.builder()
            .displayName("기존 1건 + 0건 반환")
            .userSyncStatusEntities(generateUserSyncStatusEntities())
            .mainEntities(generateMainEntities())
            .execution(exeuciton)
            .executionContext(generateExecutionContext())
            .requestParams(List.of(
                BareRequest.builder().nextPage(null).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("004_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(OLD_SYNCED_AT).build()
            ))
            .build()
        ,
        TestCase.builder()
            .displayName("기존 1건 + 추가 1건")
            .userSyncStatusEntities(generateUserSyncStatusEntities())
            .mainEntities(generateMainEntities())
            .execution(exeuciton)
            .executionContext(generateExecutionContext())
            .requestParams(List.of(
                BareRequest.builder().nextPage(null).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("005_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(OLD_SYNCED_AT).build(),
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .build()
        ,
        TestCase.builder()
            .displayName("기존 1건 + 동일 1건")
            .userSyncStatusEntities(generateUserSyncStatusEntities())
            .mainEntities(generateMainEntities())
            .execution(exeuciton)
            .executionContext(generateExecutionContext())
            .requestParams(List.of(
                BareRequest.builder().nextPage(null).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("006_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(OLD_SYNCED_AT).build()
            ))
            .build()
        ,
        TestCase.builder()
            .displayName("기존 1건 + 변경 1건")
            .userSyncStatusEntities(generateUserSyncStatusEntities())
            .mainEntities(generateMainEntities())
            .execution(exeuciton)
            .executionContext(generateExecutionContext())
            .requestParams(List.of(
                BareRequest.builder().nextPage(null).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("007_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedMains(List.of(
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .build()
    );
  }

  private static List<UserSyncStatusEntity> generateUserSyncStatusEntities() {
    return List.of(
        UserSyncStatusEntity.builder()
            .syncedAt(OLD_SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .apiId(exeuciton.getApi().getId())
            .searchTimestamp(OLD_USS_ST)
            .build()
    );
  }

  private static List<Object> generateMainEntities() {
    return List.of(
        BillEntity.builder()
            .syncedAt(OLD_SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .chargeAmt(NumberUtil.bigDecimalOf(100000, 3))
            .chargeDay(14)
            .chargeMonth(202103)
            .paidOutDate("20210314")
            .cardType("01")
            .build()
//        ,
//        BillEntity.builder()
//            .syncedAt(OLD_SYNCED_AT)
//            .banksaladUserId(BANKSALAD_USER_ID)
//            .organizationId(ORGANIZATION_ID)
//            .chargeAmt(NumberUtil.bigDecimalOf(120000, 3))
//            .chargeDay(14)
//            .chargeMonth(202102)
//            .paidOutDate("20210214")
//            .cardType("01")
//            .build()
    );
  }
}
