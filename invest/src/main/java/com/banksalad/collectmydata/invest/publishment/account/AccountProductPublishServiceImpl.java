package com.banksalad.collectmydata.invest.publishment.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountProductMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountProductRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountProductResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.INVALID_RESPONSE_CODE;

@Service
@RequiredArgsConstructor
public class AccountProductPublishServiceImpl implements AccountProductPublishService {

  private final AccountSummaryRepository accountSummaryRepository;
  private final AccountProductRepository accountProductRepository;

  private final AccountProductMapper accountProductMapper = Mappers.getMapper(AccountProductMapper.class);

  @Override
  public List<AccountProductResponse> getAccountProductResponses(long banksaladUserId, String organizationId) {

    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndProductResponseCodeNotInAndConsentIsTrue(banksaladUserId, organizationId,
            Arrays.asList(INVALID_RESPONSE_CODE));

    List<AccountProductResponse> accountProductResponses = new ArrayList<>();
    for (AccountSummaryEntity accountSummaryEntity : accountSummaryEntities) {
      accountProductRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNum(banksaladUserId, organizationId,
              accountSummaryEntity.getAccountNum())
          .stream()
          .map(accountProductMapper::entityToResponseDto)
          .forEachOrdered(accountProductResponses::add);
    }

    return accountProductResponses;
  }
}
