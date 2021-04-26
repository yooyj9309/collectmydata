package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.irp.api.AccountInfoResponsePaginationHelper;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailsResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountDetailHistoryMapper;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountDetailMapper;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IrpAccountDetailInfoResponseHelper implements
    AccountInfoResponsePaginationHelper<IrpAccountDetailRequest, IrpAccountSummary, List<IrpAccountDetail>> {

  private final IrpAccountSummaryService irpAccountSummaryService;

  private final IrpAccountDetailRepository irpAccountDetailRepository;

  private final IrpAccountDetailHistoryRepository irpAccountDetailHistoryRepository;

  private final IrpAccountDetailMapper irpAccountDetailMapper = Mappers
      .getMapper(IrpAccountDetailMapper.class);

  private final IrpAccountDetailHistoryMapper irpAccountDetailHistoryMapper = Mappers
      .getMapper(IrpAccountDetailHistoryMapper.class);

  @Override
  public List<IrpAccountDetail> getAccountFromResponse(AccountResponse accountResponse) {
    return ((IrpAccountDetailsResponse) accountResponse).getIrpAccountDetails();
  }

  @Override
  @Transactional
  public void saveAccountAndHistory(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      List<IrpAccountDetail> apiIrpAccountDetails, IrpAccountDetailRequest accountRequest) {

    /*
      Paging으로 인해 api와 db목록 전체 비교 불가 => compare~skip없이 무조건 delete~insert
      1page 이 경우에만 delete를 수행하고 이후 모든 페이지에서는 only insert수행
    */
    if (!StringUtils.hasText(accountRequest.getNextPage())) {

      irpAccountDetailRepository
          .deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
              executionContext.getBanksaladUserId(),
              executionContext.getOrganizationId(), irpAccountSummary.getAccountNum(),
              irpAccountSummary.getSeqno());
    }

    List<IrpAccountDetailEntity> dbIrpAccountDetailEntities = irpAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoOrderByIrpDetailNoDesc(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), irpAccountSummary.getAccountNum(),
            irpAccountSummary.getSeqno());

    short irpDetailNo = Integer.valueOf(dbIrpAccountDetailEntities.stream()
        .mapToInt(IrpAccountDetailEntity::getIrpDetailNo)
        .max()
        .orElse(-1)).shortValue();

    for (IrpAccountDetail irpAccountDetail : apiIrpAccountDetails) {

      // convert to entity
      IrpAccountDetailEntity irpAccountDetailEntity = irpAccountDetailMapper
          .dtoToEntity(irpAccountDetail);
      irpAccountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      irpAccountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
      irpAccountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
      irpAccountDetailEntity.setAccountNum(irpAccountSummary.getAccountNum());
      irpAccountDetailEntity.setSeqno(irpAccountSummary.getSeqno());
      irpAccountDetailEntity.setIrpDetailNo(++irpDetailNo);

      // TODO : on-demand, scheduler
      irpAccountDetailEntity.setCreatedBy(Optional.ofNullable(irpAccountDetailEntity.getCreatedBy())
          .orElseGet(() -> String.valueOf(executionContext.getBanksaladUserId())));
      irpAccountDetailEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));
      irpAccountDetailEntity.setConsentId(executionContext.getConsentId());
      irpAccountDetailEntity.setSyncRequestId(executionContext.getSyncRequestId());

      irpAccountDetailRepository.save(irpAccountDetailEntity);
      irpAccountDetailHistoryRepository.save(irpAccountDetailHistoryMapper.toHistoryEntity(irpAccountDetailEntity));
    }
  }

  @Override
  public long getSearchTimestamp(IrpAccountDetailRequest irpAccountDetailRequest) {
    return irpAccountDetailRequest.getSearchTimestamp();
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      long searchTimestamp) {
    irpAccountSummaryService
        .updateDetailSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            irpAccountSummary, searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      String responseCode) {
    irpAccountSummaryService
        .updateDetailResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            irpAccountSummary, responseCode);
  }
}
