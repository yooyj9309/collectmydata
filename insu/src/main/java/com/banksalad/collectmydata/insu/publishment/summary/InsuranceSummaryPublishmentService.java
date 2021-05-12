package com.banksalad.collectmydata.insu.publishment.summary;

import com.banksalad.collectmydata.insu.publishment.summary.dto.InsuranceSummaryPublishmentResponse;

import java.util.List;

public interface InsuranceSummaryPublishmentService {

  List<InsuranceSummaryPublishmentResponse> getInsuranceSummaryResponses(long banksaladUserId, String organizationId);
}
