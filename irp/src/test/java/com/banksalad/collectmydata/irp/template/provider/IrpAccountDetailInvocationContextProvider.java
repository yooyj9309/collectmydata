package com.banksalad.collectmydata.irp.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.template.testcase.IrpAccountDetailTestCaseGenerator;
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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_CANCELLATION;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_SUCCESS;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ZERO_USS_ST;

public class IrpAccountDetailInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.irp_get_detail;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    IrpAccountSummaryEntity parent = IrpAccountSummaryEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .accountStatus("01")
        .isConsent(true)
        .prodName("개인형 IRP 계좌1")
        .detailResponseCode(RSP_CODE_SUCCESS)
        .build();

    Map<String, IrpAccountSummaryEntity> parentMap = Map.of(
        "existingParent", parent.toBuilder().detailSearchTimestamp(OLD_ST1).build(),
        "failedExistingParent",
        parent.toBuilder().detailResponseCode(RSP_CODE_NO_ACCOUNT).detailSearchTimestamp(OLD_ST1).build(),
        "failedCancellationExistingParent",
        parent.toBuilder().detailResponseCode(RSP_CODE_CANCELLATION).detailSearchTimestamp(OLD_ST1).build(),
        "updatedExistingParent", parent.toBuilder().detailSearchTimestamp(OLD_ST2).build(),
        "newParent", parent.toBuilder().detailSearchTimestamp(ZERO_USS_ST).build(),
        "updatedNewParent", parent.toBuilder().detailSearchTimestamp(OLD_ST1).build()
    );

    IrpAccountDetailEntity main1 = IrpAccountDetailEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .irpDetailNo((short) 0)
        .irpName("irp개별운용상품1")
        .irpType("01")
        .evalAmt(new BigDecimal("10.123"))
        .invPrincipal(new BigDecimal("5000.456"))
        .fundNum(5)
        .openDate("20200228")
        .expDate("20211230")
        .intRate(new BigDecimal("14.30000"))
        .build();

    IrpAccountDetailEntity main2 = IrpAccountDetailEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .irpDetailNo((short) 1)
        .irpName("irp개별운용상품2")
        .irpType("02")
        .evalAmt(new BigDecimal("20.123"))
        .invPrincipal(new BigDecimal("1000.145"))
        .fundNum(1)
        .openDate("19900228")
        .expDate("19921230")
        .intRate(new BigDecimal("10.33000"))
        .build();

    Map<String, IrpAccountDetailEntity> mainMap = Map.of(
        "main1", main1,
        "main2", main2,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).evalAmt(new BigDecimal("20.123")).build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    IrpAccountDetailTestCaseGenerator<Object, IrpAccountSummaryEntity, IrpAccountDetailEntity, Object> generator =
        new IrpAccountDetailTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, IrpAccountSummaryEntity, IrpAccountDetailEntity, Object> testCase) {

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
