package com.banksalad.collectmydata.efin.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.efin.collect.Executions;
import com.banksalad.collectmydata.efin.common.db.entity.AccountChargeEntity;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.efin.template.testcase.AccountChargeTestCaseGenerator;
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

import static com.banksalad.collectmydata.common.util.NumberUtil.bigDecimalOf;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.ACCOUNT_ID1;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.ACCOUNT_ID2;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.SUB_KEY1;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.SUB_KEY2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;

public class AccountChargeInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.finance_efin_charge;

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
        .subKey(SUB_KEY1)
        .accountId(ACCOUNT_ID1)
        .consent(true)
        .accountStatus("01")
        .payReg(true)
        .chargeSearchTimestamp(OLD_ST1)
        .build();
    AccountSummaryEntity newParent = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .subKey(SUB_KEY2)
        .accountId(ACCOUNT_ID2)
        .consent(true)
        .accountStatus("01")
        .payReg(true)
        .chargeSearchTimestamp(null)
        .build();
    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "existingParent", existingParent,
        "failedExistingParent", existingParent.toBuilder().chargeResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent", existingParent.toBuilder().chargeSearchTimestamp(NEW_ST1).build(),
        "newParent", newParent,
        "updatedNewParent", newParent.toBuilder().chargeSearchTimestamp(OLD_ST2).build()
    );

    AccountChargeEntity existingMain = AccountChargeEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .subKey(SUB_KEY1)
        .chargeOrgCode("080")
        .chargeAccountNum("하나은행 000001")
        .chargeOption("01")
        .chargeDay(21)
        .chargeAmt(bigDecimalOf(10000,3))
        .build();
    AccountChargeEntity newMain = AccountChargeEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .subKey(SUB_KEY2)
        .chargeOrgCode("080")
        .chargeAccountNum("하나은행 000002")
        .chargeOption("01")
        .chargeDay(21)
        .chargeAmt(bigDecimalOf(10000,3))
        .build();
    Map<String, AccountChargeEntity> mainMap = Map.of(
        "existingMain", existingMain,
        "updatedExistingMain", existingMain.toBuilder().syncedAt(NEW_SYNCED_AT).chargeDay(22).build(),
        "newMain", newMain.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    AccountChargeTestCaseGenerator<Object, AccountSummaryEntity, AccountChargeEntity, Object> generator =
        new AccountChargeTestCaseGenerator<>(exeuciton, null, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, AccountSummaryEntity, AccountChargeEntity, Object> testCase) {

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
