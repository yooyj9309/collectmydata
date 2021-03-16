package com.banksalad.collectmydata.telecom;


import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.telecom.common.dto.TelecomApiResponse;

public interface TelecomApiService {

  TelecomApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws CollectException, ResponseNotOkException;
}
