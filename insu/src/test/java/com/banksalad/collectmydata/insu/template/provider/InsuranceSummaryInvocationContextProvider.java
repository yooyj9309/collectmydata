package com.banksalad.collectmydata.insu.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.template.testcase.InsuranceSummaryTestCaseGenerator;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.*;

public class InsuranceSummaryInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.insurance_get_summaries;

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

    InsuranceSummaryEntity main1 = InsuranceSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum01")
        .consent(true)
        .prodName("01")
        .insuType("01")
        .insuStatus("01")
        .build();

    InsuranceSummaryEntity main2 = InsuranceSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum02")
        .consent(true)
        .prodName("02")
        .insuType("02")
        .insuStatus("02")
        .build();

    Map<String, InsuranceSummaryEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).prodName("02").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    InsuranceSummaryTestCaseGenerator<Object, UserSyncStatusEntity, InsuranceSummaryEntity, Object> generator =
        new InsuranceSummaryTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, UserSyncStatusEntity, InsuranceSummaryEntity, Object> testCase) {

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
