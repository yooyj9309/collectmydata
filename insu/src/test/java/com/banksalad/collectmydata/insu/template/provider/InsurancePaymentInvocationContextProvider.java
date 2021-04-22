package com.banksalad.collectmydata.insu.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.template.testcase.InsurancePaymentTestCaseGenerator;
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

public class InsurancePaymentInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.insurance_get_payment;

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
        .paymentSearchTimestamp(null)
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
        .paymentSearchTimestamp(null)
        .build();

    Map<String, InsuranceSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "freshParent2", parent2,
        "failedFreshParent1", parent1.toBuilder().paymentResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedFreshParent1", parent1.toBuilder().paymentSearchTimestamp(NEW_ST1).build(),
        "updatedFreshParent2", parent2.toBuilder().paymentSearchTimestamp(NEW_ST1).build(),
        "existingParent1", parent1.toBuilder().paymentSearchTimestamp(OLD_ST1).build(),
        "failedExistingParent1",
        parent1.toBuilder().paymentSearchTimestamp(OLD_ST1).paymentResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent1", parent1.toBuilder().paymentSearchTimestamp(NEW_ST1).build()
    );

    InsurancePaymentEntity main1 = InsurancePaymentEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .payDue("01")
        .payCycle("1M")
        .payCnt(1)
        .payOrgCode("1234")
        .payDate("01")
        .payEndDate("20210320")
        .payAmt(NumberUtil.bigDecimalOf(10000, 3))
        .currencyCode("KRW")
        .autoPay(true)
        .build();

    InsurancePaymentEntity main2 = InsurancePaymentEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum2")
        .payDue("02")
        .payCycle("2M")
        .payCnt(1)
        .payOrgCode("1234")
        .payDate("02")
        .payEndDate("20210320")
        .payAmt(NumberUtil.bigDecimalOf(20000, 3))
        .currencyCode("KRW")
        .autoPay(true)
        .build();

    Map<String, InsurancePaymentEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).payCycle("2M").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    InsurancePaymentTestCaseGenerator<Object, InsuranceSummaryEntity, InsurancePaymentEntity, Object> generator =
        new InsurancePaymentTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream()
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, InsuranceSummaryEntity, InsurancePaymentEntity, Object> testCase) {

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
