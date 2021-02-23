package com.banksalad.collectmydata.referencebank.common.dto;

import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccount;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.referencebank.invest.dto.InvestAccount;
import com.banksalad.collectmydata.referencebank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.referencebank.loan.dto.LoanAccount;
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

  private List<DepositAccount> depositAccounts;

  private List<DepositAccountTransaction> depositAccountTransactions;

  private List<LoanAccount> loanAccounts;

  private List<InvestAccount> investAccounts;

  private List<InvestAccountTransaction> investAccountTransactions;

  private List<LoanAccountTransaction> loanAccountTransactions;
}
