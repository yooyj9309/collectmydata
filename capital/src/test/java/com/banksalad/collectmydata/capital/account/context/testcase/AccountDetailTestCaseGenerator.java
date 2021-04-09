package com.banksalad.collectmydata.capital.account.context.testcase;

import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.test.template.TestCaseGenerator;
import com.banksalad.collectmydata.finance.test.template.dto.BareMain;
import com.banksalad.collectmydata.finance.test.template.dto.BareParent;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

import java.util.List;

import static com.banksalad.collectmydata.capital.common.constant.CapitalConstants.ACCOUNT_NUM1;
import static com.banksalad.collectmydata.capital.common.constant.CapitalConstants.ACCOUNT_NUM2;
import static com.banksalad.collectmydata.capital.common.constant.CapitalConstants.SEQNO1;
import static com.banksalad.collectmydata.capital.common.constant.CapitalConstants.SEQNO2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_ST2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_NOT_FOUND;

/*
템플릿 메써드에 전달할 invocation context를 생성하기 위하여 모든 test case들을 여기에서 생성한다.
 */
public class AccountDetailTestCaseGenerator extends TestCaseGenerator {

  // apiId = CP03
  private static final Execution execution = Executions.capital_get_account_detail;

  public static List<TestCase> get() {
    return List.of(
        TestCase.builder()
            .displayName("존재하지 않는 계좌번호")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity()))
            .parentEntities(List.of(generateSummaryEntity1()))
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_single_page_00").status(STATUS_NOT_FOUND)
                    .rspCode(RSP_CODE_NO_ACCOUNT).build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedParents(List.of(
                BareParent.builder().syncedAt(OLD_SYNCED_AT).searchTimestamp(OLD_ST1).build()
            ))
            .build()
        ,
        TestCase.builder()
            .displayName("기존 0건 + 추가 1건")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity()))
            .parentEntities(List.of(generateSummaryEntity1()))
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("002_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedParents(List.of(
                BareParent.builder().syncedAt(OLD_SYNCED_AT).searchTimestamp(NEW_ST1).build()
            ))
            .expectedMains(List.of(
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .expectedMainEntities(List.of(
                AccountDetailEntity.builder()
                    .banksaladUserId(BANKSALAD_USER_ID)
                    .organizationId(ORGANIZATION_ID)
                    .accountNum(ACCOUNT_NUM1)
                    .seqno(SEQNO1)
                    .balanceAmt(NumberUtil.bigDecimalOf(30000.123, 3))
                    .loanPrincipal(NumberUtil.bigDecimalOf(20000.456, 3))
                    .nextRepayDate("20201114")
                    .build()
            ))
            .build()
        ,
        TestCase.builder()
            .displayName("기존 1건 + 추가 1건")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity()))
            .parentEntities(List.of(generateSummaryEntity2()))
            .mainEntities(List.of(generateMainEntity1()))
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("003_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedParents(List.of(
                BareParent.builder()
                    .syncedAt(OLD_SYNCED_AT).searchTimestamp(NEW_ST2)
                    .build()
            ))
            .expectedMains(List.of(
                BareMain.builder().syncedAt(OLD_SYNCED_AT).build(),
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .build()
        ,
        TestCase.builder()
            .displayName("기존 1건 + 동일 1건")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity()))
            .parentEntities(List.of(generateSummaryEntity1()))
            .mainEntities(List.of(generateMainEntity1()))
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("004_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedParents(List.of(
                BareParent.builder()
                    .syncedAt(OLD_SYNCED_AT).searchTimestamp(OLD_ST1)
                    .build()
            ))
            .expectedMains(List.of(
                BareMain.builder().syncedAt(OLD_SYNCED_AT).build()
            ))
            .build(),
        TestCase.builder()
            .displayName("기존 1건 + 변경 1건")
            .userSyncStatusEntities(List.of(generateUserSyncStatusEntity()))
            .parentEntities(List.of(generateSummaryEntity1()))
            .mainEntities(List.of(generateMainEntity1()))
            .execution(execution)
            .executionContext(generateExecutionContext())
            .expectedResponses(List.of(
                BareResponse.builder().mockId("005_single_page_00").build()
            ))
            .expectedUserSyncStatusSyncedAt(NEW_SYNCED_AT)
            .expectedParents(List.of(
                BareParent.builder()
                    .syncedAt(OLD_SYNCED_AT).searchTimestamp(NEW_ST1)
                    .build()
            ))
            .expectedMains(List.of(
                BareMain.builder().syncedAt(NEW_SYNCED_AT).build()
            ))
            .build()
    );
  }

  private static UserSyncStatusEntity generateUserSyncStatusEntity() {
    return UserSyncStatusEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .apiId(execution.getApi().getId())
        .searchTimestamp(OLD_USS_ST)
        .build();
  }

  private static AccountSummaryEntity generateSummaryEntity1() {
    return AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .detailSearchTimestamp(OLD_ST1)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .seqno(SEQNO1)
        .isConsent(true)
        .accountNum(ACCOUNT_NUM1)
        .seqno(SEQNO1)
        .prodName("상품명1")
        .accountType("3100")
        .accountStatus("01")
        .build();
  }

  private static AccountSummaryEntity generateSummaryEntity2() {
    return AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .detailSearchTimestamp(OLD_ST2)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .seqno(SEQNO2)
        .isConsent(true)
        .accountNum(ACCOUNT_NUM2)
        .seqno(SEQNO2)
        .prodName("상품명2")
        .accountType("3200")
        .accountStatus("03")
        .build();
  }

  private static AccountDetailEntity generateMainEntity1() {
    return AccountDetailEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM1)
        .seqno(SEQNO1)
        .balanceAmt(NumberUtil.bigDecimalOf(30000.123, 3))
        .loanPrincipal(NumberUtil.bigDecimalOf(20000.456, 3))
        .nextRepayDate("20201114")
        .build();
  }
}
