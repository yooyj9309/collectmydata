package com.banksalad.collectmydata.insu.publishment.summary;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceSummaryMapper;
import com.banksalad.collectmydata.insu.publishment.summary.dto.InsuranceSummaryPublishmentResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InsuranceSummaryPublishmentServiceImpl implements InsuranceSummaryPublishmentService {

  private final InsuranceSummaryRepository insuranceSummaryRepository;
  private final InsuranceSummaryMapper insuranceSummaryMapper = Mappers.getMapper(InsuranceSummaryMapper.class);

  @Override
  public List<InsuranceSummaryPublishmentResponse> getInsuranceSummaryResponses(long banksaladUserId,
      String organizationId) {
    return insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId)
        .stream()
        .map(insuranceSummaryMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }
}
