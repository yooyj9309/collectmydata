package com.banksalad.collectmydata.referencebank.common.dto;

import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.referencebank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.referencebank.invest.dto.InvestAccountDetail;
import com.banksalad.collectmydata.referencebank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.referencebank.loan.dto.LoanAccountBasic;
import com.banksalad.collectmydata.referencebank.loan.dto.LoanAccountDetail;
import com.banksalad.collectmydata.referencebank.loan.dto.LoanAccountTransaction;

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
public class BankApiResponse {

  private List<DepositAccountBasic> depositAccountBasics;

  private List<DepositAccountDetail> depositAccountDetails;

  private List<DepositAccountTransaction> depositAccountTransactions;

  private List<LoanAccountBasic> loanAccountBasics;

  private List<LoanAccountDetail> loanAccountDetails;

  private List<LoanAccountTransaction> loanAccountTransactions;

  private List<InvestAccountBasic> investAccountBasics;

  private List<InvestAccountDetail> investAccountDetails;

  private List<InvestAccountTransaction> investAccountTransactions;
}
