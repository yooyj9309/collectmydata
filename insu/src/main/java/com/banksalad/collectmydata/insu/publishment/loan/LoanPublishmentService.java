package com.banksalad.collectmydata.insu.publishment.loan;

import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanBasicPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanDetailPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanTransactionPublishmentResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanPublishmentService {

  List<LoanBasicPublishmentResponse> getLoanBasicResponses(long banksaladUserId, String organizationId);

  List<LoanDetailPublishmentResponse> getLoanDetailResponses(long banksaladUserId, String organizationId);

  List<LoanTransactionPublishmentResponse> getLoanTransactionResponses(long banksaladUserId,
      String organizationId, String accountNum, LocalDateTime createdAt, int limit);
}
