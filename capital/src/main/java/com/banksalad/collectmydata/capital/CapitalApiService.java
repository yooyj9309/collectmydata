package com.banksalad.collectmydata.capital;

import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

public interface CapitalApiService {

  CapitalApiResponse onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;

  CapitalApiResponse scheduledBasicRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;

  CapitalApiResponse scheduledAdditionalRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;
}
