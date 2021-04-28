package com.banksalad.collectmydata.bank.publishment.invest;

import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountTransactionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface InvestAccountPublishService {

  List<InvestAccountBasicResponse> getInvestAccountBasicResponses(long banksaladUserId, String organizationId);

  List<InvestAccountDetailResponse> getInvestAccountDetailResponses(long banksaladUserId, String organizationId);

  List<InvestAccountTransactionResponse> getInvestAccountTransactionResponses(long banksaladUserId,
      String organizationId, String accountNum, String seqno, LocalDateTime createdAt, int limit);
}
