package com.banksalad.collectmydata.insu.grpc.converter;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.insu.publishment.car.dto.CarInsurancePublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.car.dto.CarInsuranceTransactionPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceBasicPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceContractPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsurancePaymentPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceTransactionPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuredPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanBasicPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanDetailPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanTransactionInterestPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanTransactionPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.summary.dto.InsuranceSummaryPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.summary.dto.LoanSummaryPublishmentResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.CarInsurance;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.CarInsuranceTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.InsuranceBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.InsuranceContract;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.InsurancePayment;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.InsuranceSummary;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.InsuranceTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.Insured;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.LoanBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.LoanDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.LoanSummary;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.LoanTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.LoanTransactionInterest;
import com.google.protobuf.StringValue;

import static com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter.toInt64ValueMultiply1000;
import static com.banksalad.collectmydata.common.util.DateUtil.*;

@Component
public class InsuProtoConverter {

  public InsuranceSummary toInsuranceSummary(InsuranceSummaryPublishmentResponse response) {
    return InsuranceSummary.newBuilder()
        .setInsuNum(response.getInsuNum())
        .setIsConsent(response.isConsent())
        .setInsuType(response.getInsuType())
        .setProdName(response.getProdName())
        .setInsuStatus(response.getInsuStatus())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }

  public InsuranceBasic toInsuranceBasic(InsuranceBasicPublishmentResponse response) {
    InsuranceBasic.Builder insuranceBasicBuilder = InsuranceBasic.newBuilder();

    if (response.getPensionRcvStartDate() != null) {
      insuranceBasicBuilder.setPensionRcvStarDate(StringValue.of(response.getPensionRcvStartDate())); // TODO idl 오타 수정
    }
    if (response.getPensionRcvCycle() != null) {
      insuranceBasicBuilder.setPensionRcvCycle(StringValue.of(response.getPensionRcvCycle()));
    }

    insuranceBasicBuilder
        .setInsuNum(response.getInsuNum())
        .setIsRenewable(response.isRenewable())
        .setIssueDate(response.getIssueDate())
        .setExpDate(response.getExpDate())
        .setFaceAmt3F(toInt64ValueMultiply1000(response.getFaceAmt()).getValue())
        .setCurrencyCode(response.getCurrencyCode())
        .setIsVariable(response.getVariable())
        .setIsUniversal(response.getUniversal())
        .setIsLoanable(response.getLoanable())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()));

    for (InsuredPublishmentResponse insuredPublishmentResponse : response.getInsuredPublishmentResponse()) {
      insuranceBasicBuilder.addInsureds(toInsured(insuredPublishmentResponse));
    }

    return insuranceBasicBuilder.build();
  }

  private Insured toInsured(InsuredPublishmentResponse response) {
    return Insured.newBuilder()
        .setInsuNum(response.getInsuNum())
        .setInsuredNo(response.getInsuredNo())
        .setInsuredName(response.getInsuredName())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }

  public InsuranceContract toInsuranceContract(InsuranceContractPublishmentResponse response) {
    return InsuranceContract.newBuilder()
        .setInsuNum(response.getInsuNum())
        .setInsuredNo(response.getInsuredNo())
        .setContractNo(response.getContractNo())
        .setContractStatus(response.getContractStatus())
        .setContractName(response.getContractName())
        .setContractExpDate(response.getContractExpDate())
        .setContractFaceAmt3F(toInt64ValueMultiply1000(response.getContractFaceAmt()).getValue())
        .setCurrencyCode(response.getCurrencyCode())
        .setIsRequired(response.getRequired())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }

  public CarInsurance toCarInsurance(CarInsurancePublishmentResponse response) {
    return CarInsurance.newBuilder()
        .setInsuNum(response.getInsuNum())
        .setCarNumber(response.getCarNumber())
        .setCarInsuType(response.getCarInsuType())
        .setCarName(response.getCarName())
        .setStartDate(response.getStartDate())
        .setEndDate(response.getEndDate())
        .setContractAge(response.getContractAge())
        .setContractDriver(response.getContractDriver())
        .setIsOwnDmgCoverage(response.getOwnDmgCoverage())
        .setSelfPayRate(response.getSelfPayRate())
        .setSelfPayAmt3F(toInt64ValueMultiply1000(response.getSelfPayAmt()).getValue())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }

  public InsurancePayment toInsurancePayment(InsurancePaymentPublishmentResponse response) {
    InsurancePayment.Builder insurancePaymentBuilder = InsurancePayment.newBuilder();

    if (response.getPayDate() != null) {
      insurancePaymentBuilder.setPayDate(StringValue.of(response.getPayDate()));
    }

    return insurancePaymentBuilder
        .setInsuNum(response.getInsuNum())
        .setPayDue(response.getPayDue())
        .setPayCycle(response.getPayCycle())
        .setPayCnt(response.getPayCnt())
        .setPayOrgCode(response.getPayOrgCode())
        .setPayEndDate(response.getPayEndDate())
        .setPayAmt3F(toInt64ValueMultiply1000(response.getPayAmt()).getValue())
        .setCurrencyCode(response.getCurrencyCode())
        .setIsAutoPay(response.getAutoPay())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }

  public InsuranceTransaction toInsuranceTransaction(InsuranceTransactionPublishmentResponse response) {
    return InsuranceTransaction.newBuilder()
        .setInsuNum(response.getInsuNum())
        .setTransNo(response.getTransNo())
        .setTransDate(response.getTransDate())
        .setTransAppliedMonth(response.getTransAppliedMonth())
        .setPaidAmt3F(toInt64ValueMultiply1000(response.getPaidAmt()).getValue())
        .setCurrencyCode(response.getCurrencyCode())
        .setPayMethod(response.getPayMethod())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }

  public CarInsuranceTransaction toCarInsuranceTransaction(CarInsuranceTransactionPublishmentResponse response) {
    return CarInsuranceTransaction.newBuilder()
        .setInsuNum(response.getInsuNum())
        .setCarNumber(response.getCarNumber())
        .setTransNo(response.getTransNo())
        .setFaceAmt3F(toInt64ValueMultiply1000(response.getFaceAmt()).getValue())
        .setPaidAmt3F(toInt64ValueMultiply1000(response.getPaidAmt()).getValue())
        .setPayMethod(response.getPayMethod())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }

  public LoanSummary toLoanSummary(LoanSummaryPublishmentResponse response) {
    return LoanSummary.newBuilder()
        .setAccountNum(response.getAccountNum())
        .setIsConsent(response.isConsent())
        .setProdName(response.getProdName())
        .setAccountType(response.getAccountType())
        .setAccountStatus(response.getAccountStatus())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }

  public LoanBasic toLoanBasic(LoanBasicPublishmentResponse response) {
    return LoanBasic.newBuilder()
        .setAccountNum(response.getAccountNum())
        .setLoanStartDate(response.getLoanStartDate())
        .setLoanExpDate(response.getLoanExpDate())
        .setRepayMethod(response.getRepayMethod())
        .setInsuNum(response.getInsuNum())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }

  public LoanDetail toLoanDetail(LoanDetailPublishmentResponse response) {
    return LoanDetail.newBuilder()
        .setAccountNum(response.getAccountNum())
        .setCurrencyCode(response.getCurrencyCode())
        .setBalanceAmt3F(toInt64ValueMultiply1000(response.getBalanceAmt()).getValue())
        .setLoanPrincipal3F(toInt64ValueMultiply1000(response.getLoanPrincipal()).getValue())
        .setNextRepayDate(response.getNextRepayDate())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }

  public LoanTransaction toLoanTransaction(LoanTransactionPublishmentResponse response) {
    LoanTransaction.Builder loanTransactionBuilder = LoanTransaction.newBuilder()
        .setAccountNum(response.getAccountNum())
        .setTransNo(response.getTransNo())
        .setTransDtime(response.getTransDtime())
        .setCurrencyCode(response.getCurrencyCode())
        .setLoanPaidAmt3F(toInt64ValueMultiply1000(response.getLoanPaidAmt()).getValue())
        .setIntPaidAmt3F(toInt64ValueMultiply1000(response.getIntPaidAmt()).getValue())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()));

    for (LoanTransactionInterestPublishmentResponse interestResponse : response.getInterestPublishmentResponses()) {
      loanTransactionBuilder.addLoanTransactionInterests(toLoanTransactionInterest(interestResponse));
    }

    return loanTransactionBuilder.build();
  }

  private LoanTransactionInterest toLoanTransactionInterest(LoanTransactionInterestPublishmentResponse response) {
    LoanTransactionInterest.Builder loanTransactionInterestBuilder = LoanTransactionInterest.newBuilder();

    if (response.getIntRate() != null) {
      loanTransactionInterestBuilder.setIntRate3F(toInt64ValueMultiply1000(response.getIntRate()));
    }

    return loanTransactionInterestBuilder
        .setAccountNum(response.getAccountNum())
        .setTransDtime(response.getTransDtime())
        .setTransNo(response.getTransNo())
        .setIntNo(response.getIntNo())
        .setIntStartDate(response.getIntStartDate())
        .setIntEndDate(response.getIntEndDate())
        .setIntType(response.getIntType())
        .setCreatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getCreatedAt()))
        .setUpdatedAtMs(kstLocalDateTimeToEpochMilliSecond(response.getUpdatedAt()))
        .build();
  }
}
