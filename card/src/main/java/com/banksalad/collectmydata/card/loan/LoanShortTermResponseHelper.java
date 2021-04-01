package com.banksalad.collectmydata.card.loan;

import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermEntity;
import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.LoanShortTermHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.LoanShortTermRepository;
import com.banksalad.collectmydata.card.common.mapper.LoanShortTermHistoryMapper;
import com.banksalad.collectmydata.card.common.mapper.LoanShortTermMapper;
import com.banksalad.collectmydata.card.loan.dto.ListLoanShortTermsResponse;
import com.banksalad.collectmydata.card.loan.dto.LoanShortTerm;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoanShortTermResponseHelper implements UserBaseResponseHelper<List<LoanShortTerm>> {

  private final LoanShortTermMapper loanShortTermMapper = Mappers.getMapper(LoanShortTermMapper.class);
  private final LoanShortTermHistoryMapper loanShortTermHistoryMapper = Mappers
      .getMapper(LoanShortTermHistoryMapper.class);
  private final LoanShortTermRepository loanShortTermRepository;
  private final LoanShortTermHistoryRepository loanShortTermHistoryRepository;

  @Override
  public List<LoanShortTerm> getUserBaseInfoFromResponse(UserBaseResponse userBaseResponse) {

    return ((ListLoanShortTermsResponse) userBaseResponse).getShortTermList();
  }

  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, List<LoanShortTerm> loanShortTerms) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    // FIXME: 신정원 종합포털 문의결과에 따라 로직 수정해야 함
    //  현재 로직: 리스트가 동일하면 DB 업데이트 없고, 다르면 모두 삭제 후 추가한다.
    List<LoanShortTerm> existingLoanShortTerms = loanShortTermRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream().map(loanShortTermMapper::entityToDto).collect(Collectors.toList());

    if (ObjectComparator.isSameListIgnoreOrder(loanShortTerms, existingLoanShortTerms)) {
      return;
    }

    loanShortTermRepository.deleteByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId);

    List<LoanShortTermEntity> loanShortTermEntities = new ArrayList<>();
    List<LoanShortTermHistoryEntity> loanShortTermHistoryEntities = new ArrayList<>();
    for (int i = 0; i < loanShortTerms.size(); i++) {
      LoanShortTerm loanShortTerm = loanShortTerms.get(i);
      LoanShortTermEntity loanShortTermEntity = loanShortTermMapper.dtoToEntity(loanShortTerm);
      loanShortTermEntity.setSyncedAt(syncedAt);
      loanShortTermEntity.setBanksaladUserId(banksaladUserId);
      loanShortTermEntity.setOrganizationId(organizationId);
      loanShortTermEntity.setLoanShortTermNo(i);
      loanShortTermEntity.setCreatedBy(String.valueOf(banksaladUserId));
      loanShortTermEntity.setUpdatedBy(String.valueOf(banksaladUserId));

      loanShortTermEntities.add(loanShortTermEntity);
      loanShortTermHistoryEntities.add(loanShortTermHistoryMapper.toHistoryEntity(loanShortTermEntity));
    }
    loanShortTermRepository.saveAll(loanShortTermEntities);
    loanShortTermHistoryRepository.saveAll(loanShortTermHistoryEntities);
  }
}
