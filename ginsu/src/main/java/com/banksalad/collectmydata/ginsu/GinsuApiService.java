package com.banksalad.collectmydata.ginsu;

import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

public interface GinsuApiService {

  void requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException;
}
