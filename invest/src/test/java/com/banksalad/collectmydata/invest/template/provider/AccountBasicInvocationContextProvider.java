package com.banksalad.collectmydata.invest.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.template.testcase.AccountBasicTestCaseGenerator;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;

public class AccountBasicInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_invest_account_basic;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    AccountSummaryEntity existingParent = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .consent(true)
        .accountName("증권계좌1")
        .accountType("101")
        .accountStatus("201")
        .basicSearchTimestamp(OLD_ST1)
        .build();

    AccountSummaryEntity newParent = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("2222222222")
        .consent(true)
        .accountName("증권계좌2")
        .accountType("101")
        .accountStatus("201")
        .basicSearchTimestamp(0L)
        .build();

    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "existingParent", existingParent,
        "failedExistingParent", existingParent.toBuilder().basicResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent", existingParent.toBuilder().basicSearchTimestamp(NEW_ST1).build(),
        "newParent", newParent,
        "updatedNewParent", newParent.toBuilder().basicSearchTimestamp(OLD_ST2).build()
    );

    AccountBasicEntity existingMain = AccountBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .issueDate("20210101")
        .taxBenefits(true)
        .withholdingsAmt(new BigDecimal("10000.000"))
        .creditLoanAmt(new BigDecimal("20000.000"))
        .mortgageAmt(new BigDecimal("30000.000"))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .build();

    AccountBasicEntity newMain = AccountBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("2222222222")
        .issueDate("20210201")
        .taxBenefits(true)
        .withholdingsAmt(new BigDecimal("10000.000"))
        .creditLoanAmt(new BigDecimal("20000.000"))
        .mortgageAmt(new BigDecimal("30000.000"))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .build();

    Map<String, AccountBasicEntity> mainMap = Map.of(
        "existingMain", existingMain,
        "updatedExistingMain", existingMain.toBuilder().syncedAt(NEW_SYNCED_AT).withholdingsAmt(new BigDecimal("40000.000")).build(),
        "newMain", newMain.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    AccountBasicTestCaseGenerator<Object, AccountSummaryEntity, AccountBasicEntity, Object> generator =
        new AccountBasicTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(TestCase testCase) {
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
