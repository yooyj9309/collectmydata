package com.banksalad.collectmydata.telecom.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.telecom.collect.Executions;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomBillEntity;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;
import com.banksalad.collectmydata.telecom.template.testcase.TelecomBillTestCaseGenerator;

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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.banksalad.collectmydata.telecom.common.constant.TelecomTestConstants.LAST_CHARGE_MONTH;
import static com.banksalad.collectmydata.telecom.common.constant.TelecomTestConstants.LAST_LAST_CHARGE_MONTH;
import static com.banksalad.collectmydata.telecom.common.constant.TelecomTestConstants.LAST_LAST_MONTH_SYNCED_AT;
import static com.banksalad.collectmydata.telecom.common.constant.TelecomTestConstants.LAST_MONTH_SYNCED_AT;
import static com.banksalad.collectmydata.telecom.common.constant.TelecomTestConstants.MGMT_ID1;
import static com.banksalad.collectmydata.telecom.common.constant.TelecomTestConstants.MGMT_ID2;

public class TelecomBillInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_telecom_bills;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    UserSyncStatusEntity parent1 = UserSyncStatusEntity.builder()
        .syncedAt(LAST_MONTH_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .apiId(execution.getApi().getId())
        .build();
    Map<String, UserSyncStatusEntity> parentMap = Map.of(
        "parent1", parent1,
        "touchedParent1", parent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(0L).build()
    );

    TelecomBillEntity existingMain = TelecomBillEntity.builder()
        .syncedAt(LAST_LAST_MONTH_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .chargeMonth(LAST_LAST_CHARGE_MONTH)
        .mgmtId(MGMT_ID1)
        .chargeAmt(bigDecimalOf(12345, 3))
        .chargeDate("20210301")
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
    TelecomBillEntity newMain = TelecomBillEntity.builder()
        .syncedAt(NEW_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .chargeMonth(LAST_CHARGE_MONTH)
        .mgmtId(MGMT_ID2)
        .chargeAmt(bigDecimalOf(12345, 3))
        .chargeDate("20210301")
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
    Map<String, TelecomBillEntity> mainMap = Map.of(
        "existingMain", existingMain,
        "newMain", newMain,
        "updatedNewMain", newMain.toBuilder().chargeAmt(bigDecimalOf(99999, 3)).build()
    );

    TelecomBillTestCaseGenerator<Object, UserSyncStatusEntity, TelecomBillEntity, Object> generator =
        new TelecomBillTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, UserSyncStatusEntity, TelecomBillEntity, Object> testCase) {

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
