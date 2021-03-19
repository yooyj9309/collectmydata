package com.banksalad.collectmydata.capital;

import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

public interface CapitalApiService {

  CapitalApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException;
}
