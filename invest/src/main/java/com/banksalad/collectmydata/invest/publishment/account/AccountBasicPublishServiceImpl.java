package com.banksalad.collectmydata.invest.publishment.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountBasicMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountBasicResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.INVALID_RESPONSE_CODE;

@Service
@RequiredArgsConstructor
public class AccountBasicPublishServiceImpl implements AccountBasicPublishService {

  private final AccountSummaryRepository accountSummaryRepository;
  private final AccountBasicRepository accountBasicRepository;

  private final AccountBasicMapper accountBasicMapper = Mappers.getMapper(AccountBasicMapper.class);

  @Override
  public List<AccountBasicResponse> getAccountBasicResponses(long banksaladUserId, String organizationId) {

    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndBasicResponseCodeNotInAndConsentIsTrue(banksaladUserId, organizationId,
            Arrays.asList(INVALID_RESPONSE_CODE));

    List<AccountBasicResponse> accountBasicResponses = new ArrayList<>();
    for (AccountSummaryEntity accountSummaryEntity : accountSummaryEntities) {
      accountBasicRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNum(banksaladUserId, organizationId,
              accountSummaryEntity.getAccountNum())
          .ifPresent(
              accountBasicEntity -> accountBasicResponses
                  .add(accountBasicMapper.entityToResponseDto(accountBasicEntity)));
    }

    return accountBasicResponses;
  }
}
