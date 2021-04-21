package com.banksalad.collectmydata.invest.template.testcase;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_NOT_FOUND;

@RequiredArgsConstructor
public class AccountProductTestCaseGenerator <GParent, Parent, Main, Child> {

  private final Execution execution;
  private final Map<String, GParent> gParentMap;
  private final Map<String, Parent> parentMap;
  private final Map<String, Main> mainMap;
  private final Map<String, Child> childMap;

  public List<TestCase<GParent, Parent, Main, Child>> generate() {
    return List.of(
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("001. 존재하지 않는 증권번호")
            .parentEntities(List.of(parentMap.get("existingParent")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_page_01").status(STATUS_NOT_FOUND).rspCode(RSP_CODE_NO_ACCOUNT)
                    .build()
            ))
            .expectedParentEntities(List.of(parentMap.get("failedExistingParent")))
            .expectedMainEntities(List.of(mainMap.get("main1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("002. 기존 0건 + 추가 2건")
            .parentEntities(List.of(parentMap.get("newParent")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("002_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedNewParent")))
            .expectedMainEntities(List.of(mainMap.get("newMain1"), mainMap.get("newMain2")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("003. 기존 1건(삭제) + 0건")
            .parentEntities(List.of(parentMap.get("existingParent")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("003_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedExistingParent")))
            .expectedMainEntities(List.of())
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("004. 기존 1건(삭제) + 추가 2건")
            .parentEntities(List.of(parentMap.get("existingParent")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("004_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedExistingParent")))
            .expectedMainEntities(List.of(mainMap.get("newMain1"), mainMap.get("newMain2")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("005. 기존 1건 + 동일 1건")
            .parentEntities(List.of(parentMap.get("existingParent")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("005_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedExistingParent")))
            .expectedMainEntities(List.of(mainMap.get("main1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("006. 기존 1건 + 변경 1건")
            .parentEntities(List.of(parentMap.get("existingParent")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("006_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedExistingParent")))
            .expectedMainEntities(List.of(mainMap.get("updatedMain1")))
            .build()
    );
  }
}
