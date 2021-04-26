package com.banksalad.collectmydata.irp.template.testcase;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_INVALID_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_FORBIDDEN;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_NOT_FOUND;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_TOO_MANY_REQUEST;

@RequiredArgsConstructor
public class IrpAccountTransactionTestCaseGenerator<GParent, Parent, Main, Child> {

  private final Execution execution;
  private final Map<String, GParent> gParentMap;
  private final Map<String, Parent> parentMap;
  private final Map<String, Main> mainMap;
  private final Map<String, Child> childMap;

  public List<TestCase<GParent, Parent, Main, Child>> generate() {

    return List.of(
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("1. 존재하지 않는 계좌번호")
            .parentEntities(List.of(parentMap.get("existingParent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_page_00").status(STATUS_NOT_FOUND)
                    .rspCode(RSP_CODE_NO_ACCOUNT)
                    .build()
            ))
            .expectedParentEntities(List.of(parentMap.get("failedExistingParent1")))
            .expectedMainEntities(List.of(mainMap.get("main1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("2. 너무 많은 요청")
            .parentEntities(List.of(parentMap.get("existingParent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("002_page_00").status(STATUS_TOO_MANY_REQUEST)
                    .rspCode(RSP_CODE_OVER_QUOTA)
                    .build()
            ))
            .expectedParentEntities(List.of(parentMap.get("failedTooManyQuotaParent1")))
            .expectedMainEntities(List.of(mainMap.get("main1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("3. 기존 0건 + 추가 3건")
            .parentEntities(List.of(parentMap.get("freshParent1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("003_page_00").build(),
                BareResponse.builder().mockId("003_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedFreshParent1")))
            .expectedMainEntities(List.of(mainMap.get("newMain1"), mainMap.get("newMain2"), mainMap.get("newMain3")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("4. 기존 1건 + 추가 1건")
            .parentEntities(List.of(parentMap.get("existingParent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("004_page_00").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedExistingParent1")))
            .expectedMainEntities(List.of(mainMap.get("main1"), mainMap.get("newMain2")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("5. 기존 1건 + 동일 1건")
            .parentEntities(List.of(parentMap.get("existingParent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("005_page_00").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedExistingParent1")))
            .expectedMainEntities(List.of(mainMap.get("main1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("6. 기존 0건 + 추가 2건 + 실패 1건")
            .parentEntities(List.of(parentMap.get("freshParent1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("006_page_00").build(),
                BareResponse.builder().mockId("006_page_01").status(STATUS_FORBIDDEN)
                    .rspCode(RSP_CODE_INVALID_ACCOUNT)
                    .build()
            ))
            .expectedParentEntities(List.of(parentMap.get("invalidAccountFreshParent1")))
            .expectedMainEntities(List.of(mainMap.get("newMain1"), mainMap.get("newMain2")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("007. 기존 1건 + 0건 반환")
            .parentEntities(List.of(parentMap.get("existingParent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("007_page_00").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedExistingParent1")))
            .expectedMainEntities(List.of(mainMap.get("main1")))
            .build()
    );
  }
}
