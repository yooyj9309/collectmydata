package com.banksalad.collectmydata.insu.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.template.testcase.CarInsuranceTransactionTestCaseGenerator;

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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;
import static com.banksalad.collectmydata.insu.common.constant.InsuranceTestConstants.CAR_NUMBER;
import static com.banksalad.collectmydata.insu.common.constant.InsuranceTestConstants.INSU_NUM;
import static com.banksalad.collectmydata.insu.common.constant.InsuranceTestConstants.INSU_STATUS;
import static com.banksalad.collectmydata.insu.common.constant.InsuranceTestConstants.INSU_TYPE;
import static com.banksalad.collectmydata.insu.common.constant.InsuranceTestConstants.PROD_NAME;

public class CarInsuranceTransactionInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.insurance_get_car_transactions;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    InsuranceSummaryEntity gParent1 = InsuranceSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum(INSU_NUM)
        .consent(true)
        .prodName(PROD_NAME)
        .insuType(INSU_TYPE)
        .insuStatus(INSU_STATUS)
        .build();
    Map<String, InsuranceSummaryEntity> gParentMap = Map.of("existingGParent1", gParent1);

    CarInsuranceEntity parent1 = CarInsuranceEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum(INSU_NUM)
        .carNumber("60무1234")
        .carInsuType("02")
        .carName("그랜져 IG")
        .startDate("20200101")
        .endDate("20210101")
        .contractAge("21세")
        .contractDriver("가족한정")
        .ownDmgCoverage(true)
        .selfPayRate("01")
        .selfPayAmt(NumberUtil.bigDecimalOf(200000, 3))
        .transactionSyncedAt(null)
        .build();
    Map<String, CarInsuranceEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "failedFreshParent1", parent1.toBuilder().transactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        "updatedFreshParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build(),
        "existingParent1", parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).build(),
        "failedExistingParent1",
        parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).transactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        /* transaction은 summary의 syncedAt을 변경하지 않는다. updatedFreshParent1==updatedExistingParent1 */
        "updatedExistingParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build()
    );

    CarInsuranceTransactionEntity main1 = CarInsuranceTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum(INSU_NUM)
        .carNumber(CAR_NUMBER)
        .transNo(2)
        .faceAmt(NumberUtil.bigDecimalOf(900000, 3))
        .paidAmt(NumberUtil.bigDecimalOf(450000, 3))
        .payMethod("02")
        .build();
    CarInsuranceTransactionEntity main2 = CarInsuranceTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum(INSU_NUM)
        .carNumber(CAR_NUMBER)
        .transNo(3)
        .faceAmt(NumberUtil.bigDecimalOf(100000, 3))
        .paidAmt(NumberUtil.bigDecimalOf(70000, 3))
        .payMethod("04")
        .build();
    CarInsuranceTransactionEntity main3 = CarInsuranceTransactionEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum(INSU_NUM)
        .carNumber(CAR_NUMBER)
        .transNo(0)
        .faceAmt(NumberUtil.bigDecimalOf(50000, 3))
        .paidAmt(NumberUtil.bigDecimalOf(0, 3))
        .payMethod("03")
        .build();
    Map<String, CarInsuranceTransactionEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).payMethod("99").build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain3", main3.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    CarInsuranceTransactionTestCaseGenerator<InsuranceSummaryEntity, CarInsuranceEntity, CarInsuranceTransactionEntity, Object> generator =
        new CarInsuranceTransactionTestCaseGenerator<>(exeuciton, gParentMap, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<InsuranceSummaryEntity, CarInsuranceEntity, CarInsuranceTransactionEntity, Object> testCase) {

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
