package com.banksalad.collectmydata.efin.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.efin.collect.Executions;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryPayEntity;
import com.banksalad.collectmydata.efin.common.db.entity.OrganizationUserEntity;
import com.banksalad.collectmydata.efin.template.testcase.AccountSummaryTestCaseGenerator;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
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

import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.ACCOUNT_ID1;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.ACCOUNT_ID2;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.MEMBER_NAME;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.REG_DATE;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.SUB_KEY1;
import static com.banksalad.collectmydata.efin.common.constant.EfinTestConstant.SUB_KEY2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;

public class AccountSummaryInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_efin_summaries;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    UserSyncStatusEntity gParent1 = UserSyncStatusEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .apiId(execution.getApi().getId())
        .searchTimestamp(OLD_USS_ST)
        .build();
    Map<String, UserSyncStatusEntity> gParentMap = Map.of(
        "gParent1", gParent1,
        "newGParent1", gParent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(OLD_USS_ST).build(),
        "touchedGParent1", gParent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(OLD_USS_ST).build(),
        "updatedGParent1", gParent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(NEW_USS_ST).build()
    );

    OrganizationUserEntity parent1 = OrganizationUserEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .name(MEMBER_NAME)
        .regDate(REG_DATE)
        .build();
    Map<String, OrganizationUserEntity> parentMap = Map.of(
        "parent1", parent1,
        "newParent1", parent1.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    AccountSummaryEntity main1 = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .subKey(SUB_KEY1)
        .accountId(ACCOUNT_ID1)
        .consent(true)
        .accountStatus("01")
        .payReg(true)
        .build();
    AccountSummaryEntity main2 = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .subKey(SUB_KEY2)
        .accountId(ACCOUNT_ID2)
        .consent(true)
        .accountStatus("01")
        .payReg(true)
        .build();
    Map<String, AccountSummaryEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).accountStatus("02").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    AccountSummaryPayEntity child1 = AccountSummaryPayEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .subKey(SUB_KEY1)
        .accountId(ACCOUNT_ID1)
        .payOrgCode("080")
        .payId("423******1")
        .primary(true)
        .build();
    AccountSummaryPayEntity child2 = AccountSummaryPayEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .subKey(SUB_KEY2)
        .accountId(ACCOUNT_ID2)
        .payOrgCode("080")
        .payId("423******2")
        .primary(true)
        .build();
    Map<String, AccountSummaryPayEntity> childMap = Map.of(
        "child1", child1,
        "newChild1", child1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "child2", child2,
        "newChild2", child2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    AccountSummaryTestCaseGenerator<UserSyncStatusEntity, OrganizationUserEntity, AccountSummaryEntity, AccountSummaryPayEntity> generator =
        new AccountSummaryTestCaseGenerator<>(execution, gParentMap, parentMap, mainMap, childMap);

    return generator.generate().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<UserSyncStatusEntity, OrganizationUserEntity, AccountSummaryEntity, AccountSummaryPayEntity> testCase) {

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
