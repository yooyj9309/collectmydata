package com.banksalad.collectmydata.invest;

import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

public interface InvestApiService {

  void onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId, SyncRequestType syncRequestType)
      throws ResponseNotOkException;
}
