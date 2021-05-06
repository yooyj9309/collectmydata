package com.banksalad.collectmydata.insu.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.insu.template.testcase.InsuranceContractTestCaseGenerator;
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

public class InsuranceContractInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.insurance_get_contract;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    InsuranceSummaryEntity grandParent1 = InsuranceSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .consent(true)
        .insuType("01")
        .prodName("01")
        .insuStatus("01")
        .build();
    Map<String, InsuranceSummaryEntity> grandParentMap = Map.of(
        "freshGrandParent1", grandParent1
    );

    InsuredEntity parent1 = InsuredEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .insuredNo("01")
        .insuredName("kim")
        .contractSearchTimestamp(null)
        .contractResponseCode(RSP_CODE_SUCCESS)
        .build();
    InsuredEntity parent2 = InsuredEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .insuredNo("02")
        .insuredName("park")
        .contractSearchTimestamp(null)
        .contractResponseCode(RSP_CODE_SUCCESS)
        .build();
    Map<String, InsuredEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "freshParent2", parent2,
        "failedFreshParent1", parent1.toBuilder().contractResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedFreshParent1", parent1.toBuilder().contractSearchTimestamp(NEW_ST1).build(),
        "updatedFreshParent2", parent2.toBuilder().contractSearchTimestamp(NEW_ST1).build(),
        "existingParent1", parent1.toBuilder().contractSearchTimestamp(OLD_ST1).build(),
        "failedExistingParent1",
        parent1.toBuilder().contractSearchTimestamp(OLD_ST1).contractResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent1", parent1.toBuilder().contractSearchTimestamp(NEW_ST1).build()
    );

    InsuranceContractEntity main1 = InsuranceContractEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .insuredNo("01")
        .contractNo(0)
        .contractStatus("02")
        .contractName("name")
        .contractExpDate("20211231")
        .contractFaceAmt(NumberUtil.bigDecimalOf(10000, 3))
        .currencyCode("KRW")
        .required(true)
        .build();
    InsuranceContractEntity main2 = InsuranceContractEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .insuredNo("02")
        .contractNo(0)
        .contractStatus("02")
        .contractName("name")
        .contractExpDate("20211231")
        .contractFaceAmt(NumberUtil.bigDecimalOf(20000, 3))
        .currencyCode("KRW")
        .required(true)
        .build();
    Map<String, InsuranceContractEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).contractName("changed").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    InsuranceContractTestCaseGenerator<InsuranceSummaryEntity, InsuredEntity, InsuranceContractEntity, Object> generator =
        new InsuranceContractTestCaseGenerator<>(execution, grandParentMap, parentMap, mainMap, null);

    return generator.generate().stream()
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<InsuranceSummaryEntity, InsuredEntity, InsuranceContractEntity, Object> testCase) {

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
