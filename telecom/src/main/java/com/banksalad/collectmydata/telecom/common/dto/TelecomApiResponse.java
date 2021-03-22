package com.banksalad.collectmydata.telecom.common.dto;

import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBill;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomPaidTransaction;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomTransaction;

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
public class TelecomApiResponse {

  private List<TelecomBill> telecomBills;

  private List<TelecomTransaction> telecomTransactions;

  private List<TelecomPaidTransaction> telecomPaidTransactions;
}
