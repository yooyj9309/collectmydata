package com.banksalad.collectmydata.insu.template.testcase;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.BareRequest;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.*;

@RequiredArgsConstructor
public class InsuranceSummaryTestCaseGenerator<GParent, Parent, Main, Child> {

  private final Execution execution;
  private final Map<String, GParent> gParentMap;
  private final Map<String, Parent> parentMap;
  private final Map<String, Main> mainMap;
  private final Map<String, Child> childMap;

  public List<TestCase<GParent, Parent, Main, Child>> generate() {

    return List.of(
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("001. API 실패")
            .parentEntities(List.of(parentMap.get("parent1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_page_01")
                    .status(STATUS_INTERNAL_SERVER_ERROR).rspCode(
                    RSP_CODE_SYSTEM_FAILURE).build()
            ))
            .expectedParentEntities(List.of(parentMap.get("parent1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("002. 기존 0건 + 추가 2건")
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(0L).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("002_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("newParent1")))
            .expectedMainEntities(List.of(mainMap.get("newMain1"), mainMap.get("newMain2")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("003. 기존 1건 + 0건 반환")
            .parentEntities(List.of(parentMap.get("parent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("003_page_01")
                    .build()
            ))
            .expectedParentEntities(List.of(parentMap.get("touchedParent1")))
            .expectedMainEntities(List.of(mainMap.get("main1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("004. 기존 1건 + 추가 1건")
            .parentEntities(List.of(parentMap.get("parent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("004_page_01")
                    .build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedParent1")))
            .expectedMainEntities(List.of(mainMap.get("main1"), mainMap.get("newMain2")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("005. 기존 1건 + 동일 1건")
            .parentEntities(List.of(parentMap.get("parent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("005_page_01")
                    .build()
            ))
            .expectedParentEntities(List.of(parentMap.get("touchedParent1")))
            /* summary는 DB와 비교없이 무조건 save()한다. */
            .expectedMainEntities(List.of(mainMap.get("newMain1")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("006. 기존 1건 + 변경 1건")
            .parentEntities(List.of(parentMap.get("parent1")))
            .mainEntities(List.of(mainMap.get("main1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("006_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedParent1")))
            .expectedMainEntities(List.of(mainMap.get("updatedMain1")))
            .build()
    );
  }
}
