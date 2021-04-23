package com.banksalad.collectmydata.capital.template.provider;

import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.template.testcase.AccountDetailTestCaseGenerator;
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

import static com.banksalad.collectmydata.capital.common.constant.CapitalTestConstants.ACCOUNT_NUM1;
import static com.banksalad.collectmydata.capital.common.constant.CapitalTestConstants.ACCOUNT_NUM2;
import static com.banksalad.collectmydata.capital.common.constant.CapitalTestConstants.SEQNO1;
import static com.banksalad.collectmydata.capital.common.constant.CapitalTestConstants.SEQNO2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;

public class AccountDetailInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.capital_get_account_detail;

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
        .accountNum(ACCOUNT_NUM1)
        .seqno(SEQNO1)
        .isConsent(true)
        .prodName("상품명1")
        .accountType("3100")
        .accountStatus("01")
        .detailSearchTimestamp(OLD_ST1)
        .build();
    AccountSummaryEntity newParent = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM2)
        .seqno(SEQNO2)
        .isConsent(true)
        .prodName("상품명2")
        .accountType("3100")
        .accountStatus("03")
        .detailSearchTimestamp(null)
        .build();
    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "existingParent", existingParent,
        "failedExistingParent", existingParent.toBuilder().detailResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent", existingParent.toBuilder().detailSearchTimestamp(NEW_ST1).build(),
        "newParent", newParent,
        "updatedNewParent", newParent.toBuilder().detailSearchTimestamp(OLD_ST2).build()
    );

    AccountDetailEntity existingMain = AccountDetailEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM1)
        .seqno(SEQNO1)
        .balanceAmt(NumberUtil.bigDecimalOf(30000.123, 3))
        .loanPrincipal(NumberUtil.bigDecimalOf(20000.456, 3))
        .nextRepayDate("20201114")
        .build();
    AccountDetailEntity newMain = AccountDetailEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM2)
        .seqno(SEQNO2)
        .balanceAmt(NumberUtil.bigDecimalOf(15000, 3))
        .loanPrincipal(NumberUtil.bigDecimalOf(846000, 3))
        .nextRepayDate("20210401")
        .build();
    Map<String, AccountDetailEntity> mainMap = Map.of(
        "existingMain", existingMain,
        "updatedExistingMain",
        existingMain.toBuilder().syncedAt(NEW_SYNCED_AT).balanceAmt(NumberUtil.bigDecimalOf(6300, 3)).build(),
        "newMain", newMain.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    AccountDetailTestCaseGenerator<Object, AccountSummaryEntity, AccountDetailEntity, Object> generator =
        new AccountDetailTestCaseGenerator<>(exeuciton, null, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, AccountSummaryEntity, AccountDetailEntity, Object> testCase) {

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
