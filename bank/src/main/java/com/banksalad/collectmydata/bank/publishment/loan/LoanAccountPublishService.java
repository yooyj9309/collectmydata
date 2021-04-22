package com.banksalad.collectmydata.bank.publishment.loan;

import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanAccountPublishService {

  List<LoanAccountBasicResponse> getLoanAccountBasicResponses(long banksaladUserId, String organizationId);

  List<LoanAccountDetailResponse> getLoanAccountDetailResponses(long banksaladUserId, String organizationId);

  List<LoanAccountTransactionResponse> getLoanAccountTransactionResponse(long banksaladUserId, String organizationId,
      String accountNum, String seqno, LocalDateTime createdAt, int limit);
}
