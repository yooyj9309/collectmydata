package com.banksalad.collectmydata.invest.publishment.account;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountTransactionMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.INVALID_RESPONSE_CODE;

@Service
@RequiredArgsConstructor
public class AccountTransactionPublishServiceImpl implements AccountTransactionPublishService {

  private final AccountSummaryRepository accountSummaryRepository;
  private final AccountTransactionRepository accountTransactionRepository;

  private final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);

  @Override
  public List<AccountTransactionResponse> getAccountTransactionResponses(long banksaladUserId, String organizationId,
      String accountNum, LocalDateTime createdAfterMs, int limit) {

    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransactionResponseCodeNotInAndConsentIsTrue(banksaladUserId, organizationId, accountNum,
            Arrays.asList(INVALID_RESPONSE_CODE))
        .orElse(null);

    if (accountSummaryEntity == null) {
      return Collections.emptyList();
    }

    return accountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndCreatedAtAfter(banksaladUserId, organizationId,
            accountSummaryEntity.getAccountNum(), createdAfterMs, PageRequest.of(0, limit))
        .stream()
        .map(accountTransactionMapper::entityToResponseDto)
        .collect(Collectors.toList());
  }
}
