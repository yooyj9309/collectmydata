package com.banksalad.collectmydata.capital;

import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse;

public interface CapitalApiService {

  CapitalApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId);
}
