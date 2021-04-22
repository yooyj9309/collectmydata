package com.banksalad.collectmydata.capital.template.testcase;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_NOT_FOUND;

@RequiredArgsConstructor
public class OperatingLeaseBasicTestCaseGenerator<GParent, Parent, Main, Child> {

  private final Execution execution;
  private final Map<String, GParent> gParentMap;
  private final Map<String, Parent> parentMap;
  private final Map<String, Main> mainMap;
  private final Map<String, Child> childMap;

  public List<TestCase<GParent, Parent, Main, Child>> generate() {

    return List.of(
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("001. API 실패")
            .parentEntities(List.of(parentMap.get("existingParent")))
            .mainEntities(List.of(mainMap.get("existingMain")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_page_01").status(STATUS_NOT_FOUND).rspCode(RSP_CODE_NO_ACCOUNT)
                    .build()
            ))
            .expectedParentEntities(List.of(parentMap.get("failedExistingParent")))
            .expectedMainEntities(List.of(mainMap.get("existingMain")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("002. 기존 0건 + 1건 추가")
            .parentEntities(List.of(parentMap.get("newParent")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("002_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedNewParent")))
            .expectedMainEntities(List.of(mainMap.get("newMain")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("003. 기존 1건 + 추가 1건")
            .parentEntities(List.of(parentMap.get("newParent")))
            .mainEntities(List.of(mainMap.get("existingMain")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("003_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedNewParent")))
            .expectedMainEntities(List.of(mainMap.get("existingMain"), mainMap.get("newMain")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("004. 기존 1건 + 동일 1건")
            .parentEntities(List.of(parentMap.get("existingParent")))
            .mainEntities(List.of(mainMap.get("existingMain")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("004_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("existingParent")))
            .expectedMainEntities(List.of(mainMap.get("existingMain")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("005. 기존 1건 + 변경 1건")
            .parentEntities(List.of(parentMap.get("existingParent")))
            .mainEntities(List.of(mainMap.get("existingMain")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("005_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedExistingParent")))
            .expectedMainEntities(List.of(mainMap.get("updatedExistingMain")))
            .build()
    );
  }
}
