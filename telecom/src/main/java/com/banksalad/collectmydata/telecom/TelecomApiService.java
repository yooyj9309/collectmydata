package com.banksalad.collectmydata.telecom;

import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

public interface TelecomApiService {

  void onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;

  void scheduledBasicRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;

  void scheduledAdditionalRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;
}
