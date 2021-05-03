package com.banksalad.collectmydata.bank.template.provider;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;
import com.banksalad.collectmydata.bank.template.testcase.LoanAccountBasicTestCaseGenerator;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
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

public class LoanAccountBasicInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_bank_loan_account_basic;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    AccountSummaryEntity parent1 = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .consent(true)
        .seqno("1")
        .foreignDeposit(false)
        .prodName("자유입출금")
        .accountType("3001")
        .accountStatus("01")
        .basicSearchTimestamp(null)
        .build();

    AccountSummaryEntity parent2 = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum2")
        .consent(true)
        .seqno("1")
        .foreignDeposit(false)
        .prodName("자유입출금")
        .accountType("3001")
        .accountStatus("01")
        .basicSearchTimestamp(null)
        .build();

    Map<String, AccountSummaryEntity> parentMap = Map.of(
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

    LoanAccountBasicEntity main1 = LoanAccountBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .seqno("1")
        .holderName("만수르")
        .issueDate("20200101")
        .expDate("20201231")
        .lastOfferedRate(NumberUtil.bigDecimalOf(10, 3))
        .repayDate("20201114")
        .repayMethod("01")
        .repayOrgCode("B01")
        .repayAccountNum("11022212345")
        .consentId("consent_id1")
        .syncRequestId("sync_request_id1")
        .build();

    LoanAccountBasicEntity main2 = LoanAccountBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum2")
        .seqno("1")
        .holderName("만수르르")
        .issueDate("20200101")
        .expDate("20201231")
        .lastOfferedRate(NumberUtil.bigDecimalOf(10, 3))
        .repayDate("20201114")
        .repayMethod("01")
        .repayOrgCode("B01")
        .repayAccountNum("11022212345")
        .consentId("consent_id1")
        .syncRequestId("sync_request_id1")
        .build();

    Map<String, LoanAccountBasicEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).repayOrgCode("B02").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    LoanAccountBasicTestCaseGenerator<Object, AccountSummaryEntity, LoanAccountBasicEntity, Object> generator =
        new LoanAccountBasicTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream()
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, AccountSummaryEntity, LoanAccountBasicEntity, Object> testCase) {

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
