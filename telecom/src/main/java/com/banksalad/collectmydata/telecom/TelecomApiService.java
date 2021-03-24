package com.banksalad.collectmydata.telecom;

import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.telecom.common.dto.TelecomApiResponse;

public interface TelecomApiService {

  TelecomApiResponse onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;

  TelecomApiResponse scheduledBasicRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;

  TelecomApiResponse scheduledAdditionalRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;
}
