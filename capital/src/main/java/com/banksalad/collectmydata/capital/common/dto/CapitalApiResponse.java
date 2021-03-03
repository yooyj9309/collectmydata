package com.banksalad.collectmydata.capital.common.dto;

import com.banksalad.collectmydata.capital.loan.dto.LoanAccount;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLease;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
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
public class CapitalApiResponse {

  private List<LoanAccount> loanAccounts;

  private List<LoanAccountTransaction> loanAccountTransactions;

  private List<OperatingLease> operatingLeases;

  private List<OperatingLeaseTransaction> operatingLeasesTransactions;


}
