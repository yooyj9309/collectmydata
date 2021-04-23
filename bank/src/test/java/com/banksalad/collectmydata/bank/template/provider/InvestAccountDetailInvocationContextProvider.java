package com.banksalad.collectmydata.bank.template.provider;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;
import com.banksalad.collectmydata.bank.template.testcase.InvestAccountDetailTestCaseGenerator;
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

public class InvestAccountDetailInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_bank_invest_account_detail;

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
        .accountType("2001")
        .accountStatus("01")
        .detailSearchTimestamp(null)
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
        .accountType("2001")
        .accountStatus("01")
        .detailSearchTimestamp(null)
        .build();

    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "freshParent2", parent2,
        "failedFreshParent1", parent1.toBuilder().detailResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedFreshParent1", parent1.toBuilder().detailSearchTimestamp(NEW_ST1).build(),
        "updatedFreshParent2", parent2.toBuilder().detailSearchTimestamp(NEW_ST1).build(),
        "existingParent1", parent1.toBuilder().detailSearchTimestamp(OLD_ST1).build(),
        "failedExistingParent1",
        parent1.toBuilder().detailSearchTimestamp(OLD_ST1).detailResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent1", parent1.toBuilder().detailSearchTimestamp(NEW_ST1).build()
    );

    InvestAccountDetailEntity main1 = InvestAccountDetailEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .seqno("1")
        .currencyCode("KRW")
        .balanceAmt(NumberUtil.bigDecimalOf(10000, 3))
        .evalAmt(NumberUtil.bigDecimalOf(20000, 3))
        .invPrincipal(NumberUtil.bigDecimalOf(30000, 3))
        .fundNum(NumberUtil.bigDecimalOf(40000, 3))
        .consentId("consent_id1")
        .build();

    InvestAccountDetailEntity main2 = InvestAccountDetailEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum2")
        .seqno("1")
        .currencyCode("KRW")
        .balanceAmt(NumberUtil.bigDecimalOf(50000, 3))
        .evalAmt(NumberUtil.bigDecimalOf(60000, 3))
        .invPrincipal(NumberUtil.bigDecimalOf(70000, 3))
        .fundNum(NumberUtil.bigDecimalOf(80000, 3))
        .consentId("consent_id1")
        .build();

    Map<String, InvestAccountDetailEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1",
        main1.toBuilder().syncedAt(NEW_SYNCED_AT).balanceAmt(NumberUtil.bigDecimalOf(11111, 3)).build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    InvestAccountDetailTestCaseGenerator<Object, AccountSummaryEntity, InvestAccountDetailEntity, Object> generator =
        new InvestAccountDetailTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream()
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, AccountSummaryEntity, InvestAccountDetailEntity, Object> testCase) {

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
