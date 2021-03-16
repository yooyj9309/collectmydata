package com.banksalad.collectmydata.bank;

import com.banksalad.collectmydata.bank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

public interface BankApiService {

  BankApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException;
}
