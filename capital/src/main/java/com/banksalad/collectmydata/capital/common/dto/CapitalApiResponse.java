package com.banksalad.collectmydata.capital.common.dto;

import com.banksalad.collectmydata.capital.account.dto.AccountBasic;
import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasic;
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

  private List<AccountBasic> accountBasics;

  private List<AccountDetail> accountDetails;

  private List<AccountTransaction> accountTransactions;

  private List<OperatingLeaseBasic> operatingLeaseBasics;

  private List<OperatingLeaseTransaction> operatingLeasesTransactions;
}
