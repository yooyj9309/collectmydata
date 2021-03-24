package com.banksalad.collectmydata.insu;

import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.insu.common.dto.InsuApiResponse;

public interface InsuApiService {

  InsuApiResponse onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;

  InsuApiResponse scheduledBasicRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;

  InsuApiResponse scheduledAdditionalRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;
}
