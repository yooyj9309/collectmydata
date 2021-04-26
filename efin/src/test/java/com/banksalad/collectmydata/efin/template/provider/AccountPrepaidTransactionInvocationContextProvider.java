package com.banksalad.collectmydata.efin.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.efin.account.dto.AccountPrepaidTransaction;
import com.banksalad.collectmydata.efin.collect.Executions;
import com.banksalad.collectmydata.efin.common.db.entity.AccountPrepaidTransactionEntity;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.efin.template.testcase.AccountPrepaidTransactionTestCaseGenerator;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

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
import java.util.Optional;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.common.util.NumberUtil.bigDecimalOf;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.ACCOUNT_ID1;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.SUB_KEY1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;

public class AccountPrepaidTransactionInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.finance_efin_prepaid_transactions;

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
        .subKey(SUB_KEY1)
        .accountId(ACCOUNT_ID1)
        .consent(true)
        .accountStatus("01")
        .payReg(true)
        .prepaidTransactionSyncedAt(null)
        .build();
    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "failedFreshParent1", parent1.toBuilder().prepaidTransactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        "updatedFreshParent1", parent1.toBuilder().prepaidTransactionSyncedAt(NEW_SYNCED_AT).build(),
        "existingParent1", parent1.toBuilder().prepaidTransactionSyncedAt(OLD_SYNCED_AT).build(),
        "failedExistingParent1",
        parent1.toBuilder().prepaidTransactionSyncedAt(OLD_SYNCED_AT).prepaidTransactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        /* transaction은 summary의 syncedAt을 변경하지 않는다. updatedFreshParent1==updatedExistingParent1 */
        "updatedExistingParent1", parent1.toBuilder().prepaidTransactionSyncedAt(NEW_SYNCED_AT).build()
    );

    AccountPrepaidTransactionEntity main1 = AccountPrepaidTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .subKey(SUB_KEY1)
        .transType("5201")
        .fobName("가상계좌1")
        .transDtime("20210302091001")
        .transAmt(bigDecimalOf(5000,3))
        .balanceAmt(bigDecimalOf(10000,3))
        .transOrgCode("080")
        .transId("000*****")
        .build();
    main1.setTransactionYearMonth(Integer.valueOf(main1.getTransDtime().substring(0, 6)));
    main1.setUniqueTransNo(generateUniqueTransNo(main1));
    AccountPrepaidTransactionEntity main2 = AccountPrepaidTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .subKey(SUB_KEY1)
        .transType("5201")
        .fobName("가상계좌2")
        .transDtime("20210302091002")
        .transAmt(bigDecimalOf(5000,3))
        .balanceAmt(bigDecimalOf(10000,3))
        .transOrgCode("080")
        .transId("000*****")
        .build();
    main2.setTransactionYearMonth(Integer.valueOf(main2.getTransDtime().substring(0, 6)));
    main2.setUniqueTransNo(generateUniqueTransNo(main2));
    AccountPrepaidTransactionEntity main3 = AccountPrepaidTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .subKey(SUB_KEY1)
        .transType("5201")
        .fobName("가상계좌3")
        .transDtime("20210302091003")
        .transAmt(bigDecimalOf(5000,3))
        .balanceAmt(bigDecimalOf(10000,3))
        .transOrgCode("080")
        .transId("000*****")
        .build();
    main3.setTransactionYearMonth(Integer.valueOf(main3.getTransDtime().substring(0, 6)));
    main3.setUniqueTransNo(generateUniqueTransNo(main3));
    Map<String, AccountPrepaidTransactionEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).transMemo("memo1").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain3", main3.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    AccountPrepaidTransactionTestCaseGenerator<Object, AccountSummaryEntity, AccountPrepaidTransactionEntity, Object> generator =
        new AccountPrepaidTransactionTestCaseGenerator<>(exeuciton, null, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, AccountSummaryEntity, AccountPrepaidTransactionEntity, Object> testCase) {

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

  private String generateUniqueTransNo(AccountPrepaidTransactionEntity accountPrepaidTransactionEntity) {
    String transDtime = accountPrepaidTransactionEntity.getTransDtime();
    String transType = accountPrepaidTransactionEntity.getTransType();
    String transAmtString = accountPrepaidTransactionEntity.getTransAmt().toString();
    String balanceAmtString = Optional.ofNullable(accountPrepaidTransactionEntity.getBalanceAmt()).orElse(BigDecimal.ZERO)
        .toString();

    return HashUtil.hashCat(transDtime, transType, transAmtString, balanceAmtString);
  }
}
