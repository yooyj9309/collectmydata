package com.banksalad.collectmydata.bank.publishment.deposit;

import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountTransactionResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface DepositAccountPublishService {

  List<DepositAccountBasicResponse> getDepositAccountBasicResponses(long banksaladUserId, String organizationId);

  List<DepositAccountDetailResponse> getDepositAccountDetailResponses(long banksaladUserId, String organizationId);

  List<DepositAccountTransactionResponse> getDepositAccountTransactionResponses(long banksaladUserId,
      String organizationId, String accountNum, String seqno, LocalDateTime createdAt, int limit);
}
