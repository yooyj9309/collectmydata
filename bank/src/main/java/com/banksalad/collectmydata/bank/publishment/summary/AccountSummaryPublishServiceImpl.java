package com.banksalad.collectmydata.bank.publishment.summary;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummaryResponse;
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
    /* load summary entities(is_consent = true) and convert */
    return accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId).stream()
        .map(accountSummaryMapper::entityToResponseDto)
        .collect(Collectors.toList());
  }
}
