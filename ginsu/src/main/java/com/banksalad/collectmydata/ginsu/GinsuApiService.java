package com.banksalad.collectmydata.ginsu;

import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.ginsu.common.dto.GinsuApiResponse;

public interface GinsuApiService {

  GinsuApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException;
}
