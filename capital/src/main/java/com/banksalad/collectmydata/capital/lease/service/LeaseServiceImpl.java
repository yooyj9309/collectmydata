package com.banksalad.collectmydata.capital.lease.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.OperatingLeaseHistoryMapper;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.OperatingLeaseMapper;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseRepository;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.lease.dto.OperatingLeaseBasicResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaseServiceImpl implements LeaseService {

  private final ExternalApiService externalApiService;
  private final OperatingLeaseRepository operatingLeaseRepository;
  private final OperatingLeaseHistoryRepository operatingLeaseHistoryRepository;

  private final OperatingLeaseMapper operatingLeaseMapper = Mappers.getMapper(OperatingLeaseMapper.class);
  private final OperatingLeaseHistoryMapper operatingLeaseHistoryMapper = Mappers
      .getMapper(OperatingLeaseHistoryMapper.class);


  private static final String[] LEASE_RES_EXCLUDE_EQUALS_FIELD = {"rspCode", "rspMsg", "searchTimestamp"};

  @Override
  public void syncAllLeaseInfo(ExecutionContext executionContext, Organization organization,
      List<Account> accountList) {
    // TODO testCode와 함께 보충
//    syncAllLeaseBasic(executionContext, organization, accountList);
//    syncLeaseTransaction(executionContext, organization, accountList);
  }

  @Override
  public void syncLeaseBasic(ExecutionContext executionContext, Organization organization,
      List<Account> accountList) {

    for (Account account : accountList) {
      long timestamp = 0L; //TODO 계좌목록 테이블에 운용리스 관련 테이블도 있어야하지않을까 합니다.
      OperatingLeaseBasicResponse response = externalApiService
          .getOperatingLeaseBasic(executionContext, organization, account);

      OperatingLeaseEntity entity = operatingLeaseRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
              executionContext.getBanksaladUserId(),
              executionContext.getOrganizationId(),
              account.getAccountNum(),
              account.getSeqno()
          ).orElse(OperatingLeaseEntity.builder().build());

      OperatingLeaseBasicResponse entityDto = operatingLeaseMapper.entityToOperatingLeaseBasicResponse(entity);

      if (!ObjectComparator.isSame(entityDto, response, LEASE_RES_EXCLUDE_EQUALS_FIELD)) {
        // merge
        operatingLeaseMapper.merge(executionContext, account, response, entity);

        // make history
        OperatingLeaseHistoryEntity historyEntity = operatingLeaseHistoryMapper.toOperatingLeaseHistoryEntity(entity);

        // 운용리스 및 history save;
        operatingLeaseRepository.save(entity);
        operatingLeaseHistoryRepository.save(historyEntity);
      }

      // TODO account time update
      // account.setOperatingLeaseSearchTimestamp(response.getSearchTimestamp());
      // 여기서 account db update..?
    }

  }

  @Override
  public void syncLeaseTransaction(ExecutionContext executionContext, Organization organizatio,
      List<Account> accountList) {

  }
}
