package com.banksalad.collectmydata.capital.template.provider;

import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseTransactionEntity;
import com.banksalad.collectmydata.capital.template.testcase.OperatingLeaseTransactionTestCaseGenerator;
import com.banksalad.collectmydata.common.collect.execution.Execution;
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
import static com.banksalad.collectmydata.common.util.NumberUtil.bigDecimalOf;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;

public class OperatingLeaseTransactionInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.capital_get_operating_lease_transactions;

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
        .accountNum(ACCOUNT_NUM1)
        .seqno("1")
        .isConsent(true)
        .prodName("상품명1")
        .accountType("3710")
        .accountStatus("01")
        .operatingLeaseTransactionSyncedAt(null)
        .build();
    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "failedFreshParent1", parent1.toBuilder().operatingLeaseTransactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        "updatedFreshParent1", parent1.toBuilder().operatingLeaseTransactionSyncedAt(NEW_SYNCED_AT).build(),
        "existingParent1", parent1.toBuilder().operatingLeaseTransactionSyncedAt(OLD_SYNCED_AT).build(),
        "failedExistingParent1",
        parent1.toBuilder().operatingLeaseTransactionSyncedAt(OLD_SYNCED_AT)
            .operatingLeaseTransactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        /* transaction은 summary의 syncedAt을 변경하지 않는다. updatedFreshParent1==updatedExistingParent1 */
        "updatedExistingParent1", parent1.toBuilder().operatingLeaseTransactionSyncedAt(NEW_SYNCED_AT).build()
    );

    OperatingLeaseTransactionEntity main1 = OperatingLeaseTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM1)
        .seqno("1")
        .transDtime("20210217093000")
        .transNo("trans#1")
        .transType("03")
        .transAmt(bigDecimalOf(1000.3, 3))
        .build();
    main1.setTransactionYearMonth(Integer.valueOf(main1.getTransDtime().substring(0, 6)));
    OperatingLeaseTransactionEntity main2 = OperatingLeaseTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM1)
        .seqno("1")
        .transDtime("20210217093000")
        .transNo("trans#2")
        .transType("01")
        .transAmt(bigDecimalOf(0, 3))
        .build();
    main2.setTransactionYearMonth(Integer.valueOf(main1.getTransDtime().substring(0, 6)));
    OperatingLeaseTransactionEntity main3 = OperatingLeaseTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM1)
        .seqno("1")
        .transDtime("20210217093000")
        .transNo("trans#3")
        .transType("02")
        .transAmt(bigDecimalOf(999.1, 3))
        .build();
    main3.setTransactionYearMonth(Integer.valueOf(main1.getTransDtime().substring(0, 6)));
    Map<String, OperatingLeaseTransactionEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).transAmt(bigDecimalOf(100.001, 3)).build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain3", main3.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    OperatingLeaseTransactionTestCaseGenerator<Object, AccountSummaryEntity, OperatingLeaseTransactionEntity, Object> generator =
        new OperatingLeaseTransactionTestCaseGenerator<>(exeuciton, null, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, AccountSummaryEntity, OperatingLeaseTransactionEntity, Object> testCase) {

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
