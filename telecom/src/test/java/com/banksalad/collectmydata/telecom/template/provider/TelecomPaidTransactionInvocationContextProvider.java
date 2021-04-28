package com.banksalad.collectmydata.telecom.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.telecom.collect.Executions;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomPaidTransactionEntity;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;
import com.banksalad.collectmydata.telecom.template.testcase.TelecomPaidTransactionTestCaseGenerator;

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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.banksalad.collectmydata.telecom.common.constant.TelecomTestConstants.MGMT_ID1;

public class TelecomPaidTransactionInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.finance_telecom_paid_transactions;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    TelecomSummaryEntity parent1 = TelecomSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .mgmtId(MGMT_ID1)
        .consent(true)
        .telecomNum("07012345678")
        .type("02")
        .status("01")
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
    Map<String, TelecomSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "failedFreshParent1", parent1.toBuilder().paidTransactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        "updatedFreshParent1", parent1.toBuilder().paidTransactionSyncedAt(NEW_SYNCED_AT).build(),
        "existingParent1", parent1.toBuilder().paidTransactionSyncedAt(OLD_SYNCED_AT).build(),
        "failedExistingParent1",
        parent1.toBuilder().paidTransactionSyncedAt(OLD_SYNCED_AT).paidTransactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        /* transaction은 summary의 syncedAt을 변경하지 않는다. updatedFreshParent1==updatedExistingParent1 */
        "updatedExistingParent1", parent1.toBuilder().paidTransactionSyncedAt(NEW_SYNCED_AT).build()
    );

    TelecomPaidTransactionEntity main1 = TelecomPaidTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .mgmtId(MGMT_ID1)
        .transDate("20210101")
        .transAmt(bigDecimalOf(1500,3))
        .merchantName("편의점A")
        .transTitle("풍선껌A")
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
    main1.setTransactionYearMonth(Integer.parseInt(main1.getTransDate().substring(0, 6)));
    TelecomPaidTransactionEntity main2 = TelecomPaidTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .mgmtId(MGMT_ID1)
        .transDate("20210201")
        .transAmt(bigDecimalOf(1500,3))
        .merchantName("편의점A")
        .transTitle("풍선껌A")
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
    main2.setTransactionYearMonth(Integer.parseInt(main2.getTransDate().substring(0, 6)));
    TelecomPaidTransactionEntity main3 = TelecomPaidTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .mgmtId(MGMT_ID1)
        .transDate("20210301")
        .transAmt(bigDecimalOf(1500,3))
        .merchantName("편의점A")
        .transTitle("풍선껌A")
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
    main3.setTransactionYearMonth(Integer.parseInt(main3.getTransDate().substring(0, 6)));
    Map<String, TelecomPaidTransactionEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).transTitle("풍선껌B").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain3", main3.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    TelecomPaidTransactionTestCaseGenerator<Object, TelecomSummaryEntity, TelecomPaidTransactionEntity, Object> generator =
        new TelecomPaidTransactionTestCaseGenerator<>(exeuciton, null, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, TelecomSummaryEntity, TelecomPaidTransactionEntity, Object> testCase) {

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
