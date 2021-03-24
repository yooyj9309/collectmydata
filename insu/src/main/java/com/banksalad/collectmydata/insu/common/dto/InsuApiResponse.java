package com.banksalad.collectmydata.insu.common.dto;

import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import com.banksalad.collectmydata.insu.insurance.dto.InsurancePayment;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceTransaction;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InsuApiResponse {

  private List<InsuranceBasic> insuranceBasics;

  private List<InsuranceContract> insuranceContracts;

  private List<InsurancePayment> insurancePayments;

  private List<InsuranceTransaction> insuranceTransactions;

  private List<CarInsurance> carInsurances;

  private List<CarInsuranceTransaction> carInsuranceTransactions;

  private List<LoanBasic> loanBasics;

  private List<LoanDetail> loanDetails;

  private List<LoanTransaction> loanTransactions;
}
