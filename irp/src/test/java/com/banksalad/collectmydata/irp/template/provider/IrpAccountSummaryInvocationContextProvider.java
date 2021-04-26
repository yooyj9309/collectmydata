package com.banksalad.collectmydata.irp.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.template.testcase.IrpAccountSummaryTestCaseGenerator;
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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;

public class IrpAccountSummaryInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.irp_get_accounts;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    UserSyncStatusEntity parent1 = UserSyncStatusEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .apiId(execution.getApi().getId())
        .searchTimestamp(OLD_USS_ST)
        .build();

    Map<String, UserSyncStatusEntity> parentMap = Map.of(
        "parent1", parent1,
        "newParent1", parent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(OLD_USS_ST).build(),
        "touchedParent1", parent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(OLD_USS_ST).build(),
        "updatedParent1", parent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(NEW_USS_ST).build()
    );

    IrpAccountSummaryEntity main1 = IrpAccountSummaryEntity.builder()
        .id(1L)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234123412341234")
        .prodName("상품명1")
        .seqno("1")
        .isConsent(true)
        .accountStatus("01")
        .build();

    IrpAccountSummaryEntity main2 = IrpAccountSummaryEntity.builder()
        .id(1L)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234123412341234")
        .prodName("상품명2")
        .seqno("2")
        .isConsent(true)
        .accountStatus("01")
        .build();

    Map<String, IrpAccountSummaryEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).prodName("상품명1111").accountStatus("02").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    IrpAccountSummaryTestCaseGenerator<Object, UserSyncStatusEntity, IrpAccountSummaryEntity, Object> generator =
        new IrpAccountSummaryTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, UserSyncStatusEntity, IrpAccountSummaryEntity, Object> testCase) {

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
