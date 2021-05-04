package com.banksalad.collectmydata.card.template.testcase;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.BareRequest;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.agent.builder.AgentBuilder.LambdaInstrumentationStrategy;

import java.util.List;
import java.util.Map;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_SYSTEM_FAILURE;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_INTERNAL_SERVER_ERROR;

@RequiredArgsConstructor
public class PointTestCaseGenerator<GParent, Parent, Main, Child> {

  private final Execution execution;
  private final Map<String, GParent> gParentMap;
  private final Map<String, Parent> parentMap;
  private final Map<String, Main> mainMap;
  private final Map<String, Child> childMap;

  public List<TestCase<GParent, Parent, Main, Child>> generate() {

    return List.of(
        TestCase.<GParent, Parent, Main, Child>builder()
            .displayName("888. API 실패")
            .parentEntities(List.of(parentMap.get("parent1")))
            .execution(execution)
            .requestParams(List.of(
                BareRequest.builder().searchTimestamp(OLD_USS_ST).build()
            ))
            .expectedResponses(List.of(
                BareResponse.builder().mockId("001_single_page_00").status(STATUS_INTERNAL_SERVER_ERROR)
                    .rspCode(RSP_CODE_SYSTEM_FAILURE).build()
            ))
            .expectedParentEntities(List.of(parentMap.get("parent1")))
            .build()
    );
  }
}
