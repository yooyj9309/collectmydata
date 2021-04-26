package com.banksalad.collectmydata.irp.template.testcase;

import org.springframework.cloud.contract.spec.internal.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;

@RequiredArgsConstructor
public class IrpAccountBasicTestCaseGenerator<GParent, Parent, Main, Child> {

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
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_page_00").status(HttpStatus.NOT_FOUND).rspCode(RSP_CODE_NO_ACCOUNT)
                    .build()
            ))
            .expectedParentEntities(List.of(parentMap.get("failedExistingParent")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("002. 기존 0건 + 추가 1건")
            .parentEntities(List.of(parentMap.get("newParent")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("002_page_00").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedNewParent")))
            .expectedMainEntities(List.of(mainMap.get("newMain")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("003. 기존 1건 + 추가 1건")
            .parentEntities(List.of(parentMap.get("existingParent"), parentMap.get("newParent")))
            .mainEntities(List.of(mainMap.get("existingMain")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("003_page_00").build(),
                BareResponse.builder().mockId("003_page_01").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("existingParent"), parentMap.get("updatedNewParent")))
            .expectedMainEntities(List.of(mainMap.get("existingMain"), mainMap.get("newMain")))
            .build()
        ,
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("004. 기존 1건 + 동일 1건")
            .parentEntities(List.of(parentMap.get("existingParent")))
            .mainEntities(List.of(mainMap.get("existingMain")))
            .execution(execution)
            .expectedResponses(List.of(
                BareResponse.builder().mockId("004_page_00").build()
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
                BareResponse.builder().mockId("005_page_00").build()
            ))
            .expectedParentEntities(List.of(parentMap.get("updatedExistingNewParent")))
            .expectedMainEntities(List.of(mainMap.get("updatedExistingMain")))
            .build()
    );
  }
}