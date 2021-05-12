package com.banksalad.collectmydata.insu.publishment.summary;

import com.banksalad.collectmydata.insu.publishment.summary.dto.LoanSummaryPublishmentResponse;

import java.util.List;

public interface LoanSummaryPublishmentService {

  List<LoanSummaryPublishmentResponse> getLoanSummaryResponses(long banksaladUserId, String organizationId);
}
