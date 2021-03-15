package com.banksalad.collectmydata.referencebank;


import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.referencebank.common.dto.BankApiResponse;

public interface BankApiService {

  BankApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId, SyncRequestType syncRequestType)
      throws CollectException, ResponseNotOkException;

}
