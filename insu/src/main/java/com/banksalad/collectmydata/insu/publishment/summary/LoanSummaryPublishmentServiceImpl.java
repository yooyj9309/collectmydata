package com.banksalad.collectmydata.insu.publishment.summary;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.common.mapper.LoanSummaryMapper;
import com.banksalad.collectmydata.insu.publishment.summary.dto.LoanSummaryPublishmentResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanSummaryPublishmentServiceImpl implements LoanSummaryPublishmentService {

  private final LoanSummaryRepository loanSummaryRepository;
  private final LoanSummaryMapper loanSummaryMapper = Mappers.getMapper(LoanSummaryMapper.class);


  @Override
  public List<LoanSummaryPublishmentResponse> getLoanSummaryResponses(long banksaladUserId, String organizationId) {
    return loanSummaryRepository
        .findAllByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId)
        .stream()
        .map(loanSummaryMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }
}
