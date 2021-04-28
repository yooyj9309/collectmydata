package com.banksalad.collectmydata.invest.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.invest.template.testcase.AccountTransactionTestCaseGenerator;
import lombok.RequiredArgsConstructor;
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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;

@RequiredArgsConstructor
public class AccountTransactionInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_invest_account_transactions;

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
        .accountNum("1111111111")
        .consent(true)
        .accountName("증권계좌1")
        .accountType("101")
        .accountStatus("201")
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "failedFreshParent1", parent1.toBuilder().transactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        "updatedFreshParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build(),
        "existingParent1", parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).build(),
        "failedExistingParent1",
        parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT)
            .transactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        /* transaction은 summary의 syncedAt을 변경하지 않는다. updatedFreshParent1==updatedExistingParent1 */
        "updatedExistingParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build()
    );

    AccountTransactionEntity main1 = AccountTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .uniqueTransNo("66e96aece4de23a3961350d7f6463e7fdaebc823d554473c18e6b316c4dc0ff4")
        .prodCode("401")
        .transDtime("20210101000000")
        .prodName("주식1")
        .transType("301")
        .transTypeDetail("입금")
        .transNum(100L)
        .baseAmt(new BigDecimal("10000.0000"))
        .transAmt(new BigDecimal("20000.000"))
        .settleAmt(new BigDecimal("30000.000"))
        .balanceAmt(new BigDecimal("40000.000"))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
    main1.setTransactionYearMonth(Integer.valueOf(main1.getTransDtime().substring(0, 6)));

    AccountTransactionEntity main2 = AccountTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .uniqueTransNo("2ee9fd91cb7ddb1cb947d3c2412007bea497e4bbdebf5e6258b15a41ea06acd9")
        .prodCode("401")
        .transDtime("20210102000000")
        .prodName("주식2")
        .transType("301")
        .transTypeDetail("입금")
        .transNum(200L)
        .baseAmt(new BigDecimal("10000.0000"))
        .transAmt(new BigDecimal("20000.000"))
        .settleAmt(new BigDecimal("30000.000"))
        .balanceAmt(new BigDecimal("40000.000"))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
    main2.setTransactionYearMonth(Integer.valueOf(main1.getTransDtime().substring(0, 6)));

    AccountTransactionEntity main3 = AccountTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .uniqueTransNo("4d893d0500f7340d5718f322b7178f5c94d833d3864fdc7e4c9ca9f9401cd1fe")
        .prodCode("401")
        .transDtime("20210103000000")
        .prodName("주식3")
        .transType("301")
        .transTypeDetail("입금")
        .transNum(100L)
        .baseAmt(new BigDecimal("10000.0000"))
        .transAmt(new BigDecimal("20000.000"))
        .settleAmt(new BigDecimal("30000.000"))
        .balanceAmt(new BigDecimal("40000.000"))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
    main3.setTransactionYearMonth(Integer.valueOf(main1.getTransDtime().substring(0, 6)));

    Map<String, AccountTransactionEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).prodName("주식4").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain3", main3.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    AccountTransactionTestCaseGenerator<Object, AccountSummaryEntity, AccountTransactionEntity, Object> generator =
        new AccountTransactionTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream()
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, AccountSummaryEntity, AccountTransactionEntity, Object> testCase) {

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
