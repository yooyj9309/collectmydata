package com.banksalad.collectmydata.card.template.provider;

import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.ApprovalDomesticEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermEntity;
import com.banksalad.collectmydata.card.template.testcase.LoanShortTermTestCaseGenerator;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;

public class LoanShortTermInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_loan_short_terms;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    UserSyncStatusEntity parent1 = UserSyncStatusEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .apiId(execution.getApi().getId())
        .searchTimestamp(OLD_USS_ST)
        .build();
    Map<String, UserSyncStatusEntity> parentMap = Map.of(
        "parent1", parent1,
        "newParent1", parent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(OLD_USS_ST).build(),
        "touchedParent1", parent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(OLD_USS_ST).build(),
        "updatedParent1", parent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(NEW_USS_ST).build()
    );

    LoanShortTermEntity main1 = LoanShortTermEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .loanDtime(LocalDateTime.parse("20210401121212", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).toString())
        .loanAmt(NumberUtil.bigDecimalOf(1000000, 3))
        .payDueDate("2021-04-01")
        .intRate(NumberUtil.bigDecimalOf(300, 3))
        .loanShortTermNo((short) 1)
        .build();
    Map<String, LoanShortTermEntity> mainMap = Map.of(
        "main1", main1
    );

    LoanShortTermTestCaseGenerator<Object, UserSyncStatusEntity, LoanShortTermEntity, Object> generator = new LoanShortTermTestCaseGenerator<>(
        execution, null, parentMap, mainMap, null);

    return generator.generate().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, UserSyncStatusEntity, LoanShortTermEntity, Object> testCase
  ) {
    return new TestTemplateInvocationContext() {
      @Override
      public String getDisplayName(int invocationIndex) {
        return testCase.getDisplayName();
      }

      @Override
      public List<Extension> getAdditionalExtensions() {
        return Collections.singletonList(new ParameterResolver() {
          @Override
          public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
              throws ParameterResolutionException {
            return parameterContext.getParameter().getType().equals(TestCase.class);
          }

          @Override
          public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
              throws ParameterResolutionException {
            return testCase;
          }
        });
      }
    };
  }
}
