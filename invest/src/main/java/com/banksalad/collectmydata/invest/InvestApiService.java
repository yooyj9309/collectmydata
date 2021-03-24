package com.banksalad.collectmydata.invest;

import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.invest.common.dto.InvestApiResponse;

public interface InvestApiService {

  InvestApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId, SyncRequestType syncRequestType)
      throws ResponseNotOkException;
}
