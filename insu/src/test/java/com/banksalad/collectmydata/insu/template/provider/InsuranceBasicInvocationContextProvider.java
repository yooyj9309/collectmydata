package com.banksalad.collectmydata.insu.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.template.testcase.InsuranceBasicTestCaseGenerator;
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

public class InsuranceBasicInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.insurance_get_basic;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    InsuranceSummaryEntity parent1 = InsuranceSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .consent(true)
        .insuType("01")
        .prodName("01")
        .insuStatus("01")
        .basicSearchTimestamp(null)
        .basicResponseCode(RSP_CODE_SUCCESS)
        .build();

    InsuranceSummaryEntity parent2 = InsuranceSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum2")
        .consent(true)
        .insuType("02")
        .prodName("02")
        .insuStatus("02")
        .basicSearchTimestamp(null)
        .basicResponseCode(RSP_CODE_SUCCESS)
        .build();

    Map<String, InsuranceSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "freshParent2", parent2,
        "failedFreshParent1", parent1.toBuilder().basicResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedFreshParent1", parent1.toBuilder().basicSearchTimestamp(NEW_ST1).build(),
        "updatedFreshParent2", parent2.toBuilder().basicSearchTimestamp(NEW_ST1).build(),
        "existingParent1", parent1.toBuilder().basicSearchTimestamp(OLD_ST1).build(),
        "failedExistingParent1",
        parent1.toBuilder().basicSearchTimestamp(OLD_ST1).basicResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent1", parent1.toBuilder().basicSearchTimestamp(NEW_ST1).build()
    );

    InsuranceBasicEntity main1 = InsuranceBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .renewable(false)
        .issueDate("20200101")
        .expDate("99991231")
        .faceAmt(NumberUtil.bigDecimalOf(10000, 3))
        .currencyCode("KRW")
        .variable(true)
        .universal(true)
        .pensionRcvStartDate("20200101")
        .pensionRcvCycle("3M")
        .loanable(true)
        .insuredCount(1)
        .build();

    InsuranceBasicEntity main2 = InsuranceBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum2")
        .renewable(false)
        .issueDate("20200102")
        .expDate("99991231")
        .faceAmt(NumberUtil.bigDecimalOf(20000, 3))
        .currencyCode("KRW")
        .variable(true)
        .universal(true)
        .pensionRcvStartDate("20200101")
        .pensionRcvCycle("4M")
        .loanable(true)
        .insuredCount(1)
        .build();

    Map<String, InsuranceBasicEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).pensionRcvCycle("4M").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    InsuranceBasicTestCaseGenerator<Object, InsuranceSummaryEntity, InsuranceBasicEntity, Object> generator =
        new InsuranceBasicTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream()
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, InsuranceSummaryEntity, InsuranceBasicEntity, Object> testCase) {

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
