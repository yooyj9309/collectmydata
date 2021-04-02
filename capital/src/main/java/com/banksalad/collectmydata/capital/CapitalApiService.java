package com.banksalad.collectmydata.capital;

import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

public interface CapitalApiService {

  CapitalApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException, CollectException;
}
