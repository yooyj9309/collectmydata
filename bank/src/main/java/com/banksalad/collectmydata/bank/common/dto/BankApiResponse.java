package com.banksalad.collectmydata.bank.common.dto;

import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountBasic;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountDetail;
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

  // TODO jayden-lee 수신, 대출, 투자, IRP 계좌 정보와 거래내역 목록 프로퍼티 추가
  private List<DepositAccountBasic> depositAccountBasics;
  private List<DepositAccountDetail> depositAccountDetails;
  private List<DepositAccountTransaction> depositAccountTransactions;

  private List<InvestAccountBasic> investAccountBasics;
  private List<InvestAccountDetail> investAccountDetails;
  private List<InvestAccountTransaction> investAccountTransactions;

  private List<LoanAccountBasic> loanAccountBasics;
  private List<LoanAccountDetail> loanAccountDetails;
}
