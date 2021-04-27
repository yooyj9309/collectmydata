package com.banksalad.collectmydata.invest.publishment.summary;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.publishment.summary.dto.AccountSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountSummaryPublishServiceImpl implements AccountSummaryPublishService {

  private final AccountSummaryRepository accountSummaryRepository;

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  @Override
  public List<AccountSummaryResponse> getAccountSummaryResponses(long banksaladUserId, String organizationId) {

    return accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId)
        .stream()
        .map(accountSummaryMapper::entityToResponseDto)
        .collect(Collectors.toList());
  }
}
