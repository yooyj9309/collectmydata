package com.banksalad.collectmydata.insu;

import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

public interface InsuApiService {

  void onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;

  void scheduledBasicRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;

  void scheduledAdditionalRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;
}
