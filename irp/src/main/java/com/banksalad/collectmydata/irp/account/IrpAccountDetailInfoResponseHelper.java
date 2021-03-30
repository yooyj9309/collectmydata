package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailsResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountDetailHistoryMapper;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountDetailMapper;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.custom.BigDecimalComparatorWithFixedEquals;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IrpAccountDetailInfoResponseHelper implements
    AccountInfoResponseHelper<IrpAccountSummary, List<IrpAccountDetail>> {

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
  public void saveAccountAndHistory(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      List<IrpAccountDetail> apiIrpAccountDetails) {

    List<IrpAccountDetail> sortedApiIrpAccountDetails = new ArrayList<>(apiIrpAccountDetails);
    sortedApiIrpAccountDetails.sort(getComparator());

    Javers javers = JaversBuilder.javers()
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
        .registerValue(BigDecimal.class, new BigDecimalComparatorWithFixedEquals())
        .build();

    List<IrpAccountDetail> dbIrpAccountDetails = irpAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), irpAccountSummary.getAccountNum(),
            irpAccountSummary.getSeqno())
        .stream()
        .map(irpAccountDetailMapper::entityToDto)
        .sorted(getComparator())
        .collect(Collectors.toList());

    Diff diff = javers.compareCollections(dbIrpAccountDetails, sortedApiIrpAccountDetails, IrpAccountDetail.class);

    if (diff.getChanges().size() > 0) {

      irpAccountDetailRepository
          .deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(executionContext.getBanksaladUserId(),
              executionContext.getOrganizationId(), irpAccountSummary.getAccountNum(),
              irpAccountSummary.getSeqno());

      short irpDetailNo = 1;
      for (IrpAccountDetail irpAccountDetail : apiIrpAccountDetails) {

        // convert to entity
        IrpAccountDetailEntity irpAccountDetailEntity = irpAccountDetailMapper
            .dtoToEntity(irpAccountDetail);
        irpAccountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
        irpAccountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
        irpAccountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
        irpAccountDetailEntity.setAccountNum(irpAccountSummary.getAccountNum());
        irpAccountDetailEntity.setSeqno(irpAccountSummary.getSeqno());
        irpAccountDetailEntity.setIrpDetailNo(irpDetailNo++);

        irpAccountDetailRepository.save(irpAccountDetailEntity);

        irpAccountDetailHistoryRepository
            .save(irpAccountDetailHistoryMapper.toHistoryEntity(irpAccountDetailEntity));
      }
    }
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

  private Comparator<IrpAccountDetail> getComparator() {

    return Comparator.comparing(IrpAccountDetail::getOpenDate)
        .thenComparing(IrpAccountDetail::getIrpName)
        .thenComparing(IrpAccountDetail::getIrpType)
        .thenComparing(IrpAccountDetail::getEvalAmt)
        .thenComparing(IrpAccountDetail::getInvPrincipal)
        .thenComparing(IrpAccountDetail::getFundNum)
        .thenComparing(IrpAccountDetail::getIntRate);
  }
}
