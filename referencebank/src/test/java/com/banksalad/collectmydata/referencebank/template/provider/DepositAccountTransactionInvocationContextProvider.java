package com.banksalad.collectmydata.referencebank.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.referencebank.collect.Executions;
import com.banksalad.collectmydata.referencebank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountTransactionEntity;
import com.banksalad.collectmydata.referencebank.template.testcase.DepositAccountTransactionTestCaseGenerator;

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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;

public class DepositAccountTransactionInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.finance_bank_deposit_account_transaction;

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
        .isConsent(true)
        .isForeignDeposit(false)
        .prodName("뱅크샐러드 대박 적금")
        .accountType("1003")
        .accountStatus("01")
        .transactionSyncedAt(null)
        .build();
    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "failedFreshParent1", parent1.toBuilder().transactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        "updatedFreshParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build(),
        "existingParent1", parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).build(),
        "failedExistingParent1",
        parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).transactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        /* transaction은 summary의 syncedAt을 변경하지 않는다. updatedFreshParent1==updatedExistingParent1 */
        "updatedExistingParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build()
    );

    DepositAccountTransactionEntity main1 = DepositAccountTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .transDtime("20210102102000")
        .transType("03")
        .transClass("입금")
        .transAmt(NumberUtil.bigDecimalOf(10000, 3))
        .balanceAmt(NumberUtil.bigDecimalOf(30000, 3))
        .build();
    main1.setTransactionYearMonth(Integer.valueOf(main1.getTransDtime().substring(0, 6)));
    main1.setUniqueTransNo(HashUtil.hashCat(main1.getTransDtime(), main1.getTransType(), main1.getTransAmt().toString(),
        main1.getBalanceAmt().toString()));
    DepositAccountTransactionEntity main2 = DepositAccountTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .transDtime("20210102232000")
        .transType("02")
        .transClass("출금")
        .transAmt(NumberUtil.bigDecimalOf(10000, 3))
        .balanceAmt(NumberUtil.bigDecimalOf(20000, 3))
        .build();
    main2.setTransactionYearMonth(Integer.valueOf(main2.getTransDtime().substring(0, 6)));
    main2.setUniqueTransNo(HashUtil.hashCat(main2.getTransDtime(), main2.getTransType(), main2.getTransAmt().toString(),
        main2.getBalanceAmt().toString()));
    DepositAccountTransactionEntity main3 = DepositAccountTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .transDtime("20210102232000")
        .transType("01")
        .transClass("신규")
        .transAmt(NumberUtil.bigDecimalOf(50000, 3))
        .balanceAmt(NumberUtil.bigDecimalOf(50000, 3))
        .build();
    main3.setTransactionYearMonth(Integer.valueOf(main3.getTransDtime().substring(0, 6)));
    main3.setUniqueTransNo(HashUtil.hashCat(main3.getTransDtime(), main3.getTransType(), main3.getTransAmt().toString(),
        main3.getBalanceAmt().toString()));
    Map<String, DepositAccountTransactionEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).paidInCnt((short) 2).build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain3", main3.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    DepositAccountTransactionTestCaseGenerator<Object, AccountSummaryEntity, DepositAccountTransactionEntity, Object> generator =
        new DepositAccountTransactionTestCaseGenerator<>(exeuciton, null, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, AccountSummaryEntity, DepositAccountTransactionEntity, Object> testCase) {

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
