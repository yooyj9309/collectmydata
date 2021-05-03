package com.banksalad.collectmydata.bank.template.provider;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountTransactionEntity;
import com.banksalad.collectmydata.bank.template.testcase.InvestAccountTransactionTestCaseGenerator;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.crypto.HashUtil;
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

public class InvestAccountTransactionInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_bank_invest_account_transaction;

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
        .transactionSyncedAt(null)
        .build();
    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "failedFreshParent1", parent1.toBuilder().transactionResponseCode(RSP_CODE_INVALID_ACCOUNT).build(),
        "updatedFreshParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build(),
        "existingParent1", parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).build(),
        "failedExistingParent1",
        parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).transactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        /* transaction은 summary의 syncedAt을 변경하지 않는다. updatedFreshParent1==updatedExistingParent1 */
        "updatedExistingParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build()
    );

    InvestAccountTransactionEntity main1 = InvestAccountTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .seqno("1")
        .currencyCode("KRW")
        .transDtime("20210102102000")
        .transNo("trans#1")
        .transType("01")
        .baseAmt(NumberUtil.bigDecimalOf(10000, 3))
        .transFundNum(NumberUtil.bigDecimalOf(20000, 3))
        .transAmt(NumberUtil.bigDecimalOf(30000, 3))
        .balanceAmt(NumberUtil.bigDecimalOf(40000, 3))
        .consentId("consent_id1")
        .syncRequestId("sync_request_id1")
        .build();
    main1.setTransactionYearMonth(Integer.valueOf(main1.getTransDtime().substring(0, 6)));
    main1.setUniqueTransNo(HashUtil.hashCat(main1.getTransDtime(), main1.getTransType(), main1.getTransAmt().toString(),
        main1.getBalanceAmt().toString()));

    InvestAccountTransactionEntity main2 = InvestAccountTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .seqno("1")
        .currencyCode("KRW")
        .transDtime("20210102232000")
        .transNo("trans#2")
        .transType("02")
        .baseAmt(NumberUtil.bigDecimalOf(50000, 3))
        .transFundNum(NumberUtil.bigDecimalOf(60000, 3))
        .transAmt(NumberUtil.bigDecimalOf(70000, 3))
        .balanceAmt(NumberUtil.bigDecimalOf(80000, 3))
        .consentId("consent_id1")
        .syncRequestId("sync_request_id1")
        .build();
    main2.setTransactionYearMonth(Integer.valueOf(main2.getTransDtime().substring(0, 6)));
    main2.setUniqueTransNo(HashUtil.hashCat(main2.getTransDtime(), main2.getTransType(), main2.getTransAmt().toString(),
        main2.getBalanceAmt().toString()));

    InvestAccountTransactionEntity main3 = InvestAccountTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("accountNum1")
        .seqno("1")
        .currencyCode("KRW")
        .transDtime("20210102232000")
        .transNo("trans#3")
        .transType("03")
        .baseAmt(NumberUtil.bigDecimalOf(90000, 3))
        .transFundNum(NumberUtil.bigDecimalOf(10000, 3))
        .transAmt(NumberUtil.bigDecimalOf(11000, 3))
        .balanceAmt(NumberUtil.bigDecimalOf(12000, 3))
        .consentId("consent_id1")
        .syncRequestId("sync_request_id1")
        .build();
    main3.setTransactionYearMonth(Integer.valueOf(main3.getTransDtime().substring(0, 6)));
    main3.setUniqueTransNo(HashUtil.hashCat(main3.getTransDtime(), main3.getTransType(), main3.getTransAmt().toString(),
        main3.getBalanceAmt().toString()));

    Map<String, InvestAccountTransactionEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).baseAmt(NumberUtil.bigDecimalOf(12222, 3)).build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain3", main3.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    InvestAccountTransactionTestCaseGenerator<Object, AccountSummaryEntity, InvestAccountTransactionEntity, Object> generator =
        new InvestAccountTransactionTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream()
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, AccountSummaryEntity, InvestAccountTransactionEntity, Object> testCase) {

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
