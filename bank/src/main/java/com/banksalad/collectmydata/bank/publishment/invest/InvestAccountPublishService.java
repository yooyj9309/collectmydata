package com.banksalad.collectmydata.bank.publishment.invest;

import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountTransactionResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountDetailsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountTransactionsRequest;

import java.util.List;

public interface InvestAccountPublishService {

  List<InvestAccountBasicResponse> getInvestAccountBasicResponses(ListBankInvestAccountBasicsRequest request);

  List<InvestAccountDetailResponse> getInvestAccountDetailResponses(ListBankInvestAccountDetailsRequest request);

  List<InvestAccountTransactionResponse> getInvestAccountTransactionResponses(ListBankInvestAccountTransactionsRequest request);
}
