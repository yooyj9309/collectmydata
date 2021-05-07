package com.banksalad.collectmydata.insu.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.template.testcase.LoanBasicTestCaseGenerator;
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

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_SUCCESS;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;

public class LoanBasicInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.insurance_get_loan_basic;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    LoanSummaryEntity parent1 = LoanSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .consent(true)
        .prodName("01")
        .accountType("01")
        .accountStatus("01")
        .basicSearchTimestamp(null)
        .basicResponseCode(RSP_CODE_SUCCESS)
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();

    LoanSummaryEntity parent2 = LoanSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum2")
        .consent(true)
        .prodName("02")
        .accountType("02")
        .accountStatus("02")
        .basicSearchTimestamp(null)
        .basicResponseCode(RSP_CODE_SUCCESS)
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();

    Map<String, LoanSummaryEntity> parentMap = Map.of(
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

    LoanBasicEntity main1 = LoanBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .loanStartDate("20200101")
        .loanExpDate("20200131")
        .repayMethod("01")
        .insuNum("01")
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();

    LoanBasicEntity main2 = LoanBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum2")
        .loanStartDate("20200101")
        .loanExpDate("20200131")
        .repayMethod("02")
        .insuNum("02")
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();

    Map<String, LoanBasicEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).repayMethod("02").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    LoanBasicTestCaseGenerator<Object, LoanSummaryEntity, LoanBasicEntity, Object> generator =
        new LoanBasicTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream()
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, LoanSummaryEntity, LoanBasicEntity, Object> testCase) {

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
