package com.banksalad.collectmydata.efin.template.testcase;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.BareRequest;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_SYSTEM_FAILURE;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_INTERNAL_SERVER_ERROR;

/*
템플릿 메써드에 전달할 invocation context를 생성하기 위하여 모든 test case들을 여기에서 생성한다.
 */
@RequiredArgsConstructor
public class AccountSummaryTestCaseGenerator<GParent, Parent, Main, Child> {

  private final Execution execution;
  private final Map<String, GParent> gParentMap;
  private final Map<String, Parent> parentMap;
  private final Map<String, Main> mainMap;
  private final Map<String, Child> childMap;

  public List<TestCase<GParent, Parent, Main, Child>> generate() {

    return List.of(
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("001. API 실패")
            .gParentEntities(List.of(gParentMap.get("gParent1")))
            .parentEntities(List.of(parentMap.get("parent1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).nextPage(null).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_page_01")
                    .status(STATUS_INTERNAL_SERVER_ERROR).rspCode(RSP_CODE_SYSTEM_FAILURE).build()
            ))
            .expectedGParentEntities(List.of(gParentMap.get("gParent1")))
            .expectedParentEntities(List.of(parentMap.get("parent1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("002. 기존 0건 + 1건 추가")
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(0L).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("002_page_01").build()
            ))
            .expectedGParentEntities(List.of(gParentMap.get("newGParent1")))
            .expectedParentEntities(List.of(parentMap.get("newParent1")))
            .expectedMainEntities(List.of(mainMap.get("newMain1")))
            .expectedChildEntities(List.of(childMap.get("newChild1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("003. 기존 1건 + 0건 반환")
            .gParentEntities(List.of(gParentMap.get("gParent1")))
            .parentEntities(List.of(parentMap.get("parent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .childEntities(List.of(childMap.get("child1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).nextPage(null).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("003_page_01").build()
            ))
            .expectedGParentEntities(List.of(gParentMap.get("touchedGParent1")))
            .expectedParentEntities(List.of(parentMap.get("parent1")))
            .expectedMainEntities(List.of(mainMap.get("main1")))
            .expectedChildEntities(List.of(childMap.get("child1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("004. 기존 1건 + 추가 1건")
            .gParentEntities(List.of(gParentMap.get("gParent1")))
            .parentEntities(List.of(parentMap.get("parent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .childEntities(List.of(childMap.get("child1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).nextPage(null).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("004_page_01").build()
            ))
            .expectedGParentEntities(List.of(gParentMap.get("updatedGParent1")))
            .expectedParentEntities(List.of(parentMap.get("parent1")))
            .expectedMainEntities(List.of(mainMap.get("main1"), mainMap.get("newMain2")))
            .expectedChildEntities(List.of(childMap.get("child1"), childMap.get("newChild2")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("005. 기존 1건 + 동일 1건")
            .gParentEntities(List.of(gParentMap.get("gParent1")))
            .parentEntities(List.of(parentMap.get("parent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .childEntities(List.of(childMap.get("child1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).nextPage(null).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("005_page_01").build()
            ))
            .expectedGParentEntities(List.of(gParentMap.get("touchedGParent1")))
            .expectedParentEntities(List.of(parentMap.get("parent1")))
            /* summary는 DB와 비교없이 무조건 save()한다. */
            .expectedMainEntities(List.of(mainMap.get("newMain1")))
            .expectedChildEntities(List.of(childMap.get("child1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("006. 기존 1건 + 변경 1건")
            .gParentEntities(List.of(gParentMap.get("gParent1")))
            .parentEntities(List.of(parentMap.get("parent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .childEntities(List.of(childMap.get("child1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).nextPage(null).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("006_page_01").build()
            ))
            .expectedGParentEntities(List.of(gParentMap.get("updatedGParent1")))
            .expectedParentEntities(List.of(parentMap.get("parent1")))
            .expectedMainEntities(List.of(mainMap.get("updatedMain1")))
            .expectedChildEntities(List.of(childMap.get("child1")))
            .build()
    );
  }
}
