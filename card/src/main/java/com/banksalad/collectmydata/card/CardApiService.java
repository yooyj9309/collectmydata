package com.banksalad.collectmydata.card;

import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

public interface CardApiService {

  void requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException;
}
