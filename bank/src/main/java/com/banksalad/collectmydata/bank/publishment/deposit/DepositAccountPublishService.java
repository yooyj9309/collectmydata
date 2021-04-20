package com.banksalad.collectmydata.bank.publishment.deposit;

import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountTransactionResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountDetailsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountTransactionsRequest;

import java.util.List;

public interface DepositAccountPublishService {

  List<DepositAccountBasicResponse> getDepositAccountBasicResponses(ListBankDepositAccountBasicsRequest request);

  List<DepositAccountDetailResponse> getDepositAccountDetailResponses(ListBankDepositAccountDetailsRequest request);

  List<DepositAccountTransactionResponse> getDepositAccountTransactionResponses(ListBankDepositAccountTransactionsRequest request);
}
