package com.banksalad.collectmydata.insu.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceTransactionEntity;
import com.banksalad.collectmydata.insu.template.testcase.InsuranceTransactionTestCaseGenerator;
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

public class InsuranceTransactionInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.insurance_get_transactions;

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
        .transactionSyncedAt(null)
        .transactionResponseCode(RSP_CODE_SUCCESS)
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();
    Map<String, InsuranceSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "failedFreshParent1", parent1.toBuilder().transactionResponseCode(RSP_CODE_INVALID_ACCOUNT).build(),
        "updatedFreshParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build(),
        "existingParent1", parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).build(),
        "failedExistingParent1",
        parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).transactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        /* transaction은 summary의 syncedAt을 변경하지 않는다. updatedFreshParent1==updatedExistingParent1 */
        "updatedExistingParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build()
    );

    InsuranceTransactionEntity main1 = InsuranceTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .transNo(1)
        .transDate("20210101")
        .transAppliedMonth(202101)
        .paidAmt(NumberUtil.bigDecimalOf(10000, 3))
        .currencyCode("KRW")
        .payMethod("01")
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();
    main1.setTransactionYearMonth(main1.getTransAppliedMonth());

    InsuranceTransactionEntity main2 = InsuranceTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .transNo(2)
        .transDate("20210102")
        .transAppliedMonth(202101)
        .paidAmt(NumberUtil.bigDecimalOf(20000, 3))
        .currencyCode("KRW")
        .payMethod("02")
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();
    main2.setTransactionYearMonth(main2.getTransAppliedMonth());

    InsuranceTransactionEntity main3 = InsuranceTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("insuNum1")
        .transNo(3)
        .transDate("20210103")
        .transAppliedMonth(202101)
        .paidAmt(NumberUtil.bigDecimalOf(30000, 3))
        .currencyCode("KRW")
        .payMethod("02")
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();
    main3.setTransactionYearMonth(main2.getTransAppliedMonth());

    Map<String, InsuranceTransactionEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1",
        main1.toBuilder().syncedAt(NEW_SYNCED_AT).paidAmt(NumberUtil.bigDecimalOf(11111, 3)).build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain3", main3.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    InsuranceTransactionTestCaseGenerator<Object, InsuranceSummaryEntity, InsuranceTransactionEntity, Object> generator =
        new InsuranceTransactionTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream()
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, InsuranceSummaryEntity, InsuranceTransactionEntity, Object> testCase) {

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
