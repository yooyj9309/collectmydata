package com.banksalad.collectmydata.irp.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.template.testcase.IrpAccountBasicTestCaseGenerator;
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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_SUCCESS;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ZERO_USS_ST;

public class IrpAccountBasicInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.irp_get_basic;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    IrpAccountSummaryEntity existingParent = IrpAccountSummaryEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .accountStatus("01")
        .basicSearchTimestamp(OLD_ST1)
        .basicResponseCode(RSP_CODE_SUCCESS)
        .detailSearchTimestamp(ZERO_USS_ST)
        .transactionSyncedAt(null)
        .isConsent(true)
        .prodName("개인형 IRP 계좌1")
        .seqno("a123")
        .build();

    IrpAccountSummaryEntity newParent = IrpAccountSummaryEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("234246541143")
        .accountStatus("01")
        .basicSearchTimestamp(ZERO_USS_ST)
        .basicResponseCode(RSP_CODE_SUCCESS)
        .detailSearchTimestamp(ZERO_USS_ST)
        .transactionSyncedAt(null)
        .isConsent(true)
        .prodName("개인형 IRP 계좌2")
        .seqno("a124")
        .build();

    Map<String, IrpAccountSummaryEntity> parentMap = Map.of(
        "existingParent", existingParent,
        "failedExistingParent", existingParent.toBuilder().basicResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent", existingParent.toBuilder().basicSearchTimestamp(OLD_ST1).build(),
        "updatedExistingNewParent", existingParent.toBuilder().basicSearchTimestamp(OLD_ST2).build(),
        "newParent", newParent,
        "updatedNewParent", newParent.toBuilder().basicSearchTimestamp(OLD_ST1).build()
    );

    IrpAccountBasicEntity existingMain = IrpAccountBasicEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .accumAmt(new BigDecimal("10.123"))
        .evalAmt(new BigDecimal("11.123"))
        .employerAmt(new BigDecimal("12.123"))
        .employeeAmt(new BigDecimal("13.123"))
        .issueDate("20200204")
        .firstDepositDate("20200204")
        .build();

    IrpAccountBasicEntity newMain = IrpAccountBasicEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("234246541143")
        .seqno("a124")
        .accumAmt(new BigDecimal("20.123"))
        .evalAmt(new BigDecimal("21.123"))
        .employerAmt(new BigDecimal("22.123"))
        .employeeAmt(new BigDecimal("23.123"))
        .issueDate("20210204")
        .firstDepositDate("20210204")
        .build();

    Map<String, IrpAccountBasicEntity> mainMap = Map.of(
        "existingMain", existingMain,
        "updatedExistingMain",
        existingMain.toBuilder().syncedAt(NEW_SYNCED_AT)
            .accumAmt(existingMain.getAccumAmt().add(new BigDecimal(10)))
            .employeeAmt(existingMain.getEmployeeAmt().add(new BigDecimal(10)))
            .employerAmt(existingMain.getEmployerAmt().add(new BigDecimal(10)))
            .evalAmt(existingMain.getEvalAmt().add(new BigDecimal(10)))
            .issueDate("20210204")
            .firstDepositDate("20210204")
            .build(),
        "newMain", newMain.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    IrpAccountBasicTestCaseGenerator<Object, IrpAccountSummaryEntity, IrpAccountBasicEntity, Object> generator =
        new IrpAccountBasicTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, IrpAccountSummaryEntity, IrpAccountBasicEntity, Object> testCase) {
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
