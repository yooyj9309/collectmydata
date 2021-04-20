package com.banksalad.collectmydata.bank.publishment.summary;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.bank.grpc.client.ConnectClientService;
import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummaryResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankAccountSummariesRequest;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountSummaryPublishServiceImpl implements AccountSummaryPublishService {

  private final AccountSummaryRepository accountSummaryRepository;
  private final ConnectClientService connectClientService;

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  @Override
  public List<AccountSummaryResponse> getAccountSummaryResponses(ListBankAccountSummariesRequest request) {
    /* type casting */
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    String organizationId = connectClientService.getOrganizationResponse(request.getOrganizationObjectid())
        .getOrganizationId();

    /* load summary entities (is_consent = true) */
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId);

    List<AccountSummaryResponse> accountSummaryResponses = new ArrayList<>();
    for (AccountSummaryEntity accountSummaryEntity : accountSummaryEntities) {
      accountSummaryResponses.add(accountSummaryMapper.entityToResponseDto(accountSummaryEntity));
    }
    return accountSummaryResponses;
  }
}
