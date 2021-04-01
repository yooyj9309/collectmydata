package com.banksalad.collectmydata.card.loan;

import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermEntity;
import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.LoanLongTermHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.LoanLongTermRepository;
import com.banksalad.collectmydata.card.common.mapper.LoanLongTermHistoryMapper;
import com.banksalad.collectmydata.card.common.mapper.LoanLongTermMapper;
import com.banksalad.collectmydata.card.loan.dto.ListLoanLongTermsResponse;
import com.banksalad.collectmydata.card.loan.dto.LoanLongTerm;
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
public class LoanLongTermResponseHelper implements UserBaseResponseHelper<List<LoanLongTerm>> {

  private final LoanLongTermMapper loanLongTermMapper = Mappers.getMapper(LoanLongTermMapper.class);
  private final LoanLongTermHistoryMapper loanLongTermHistoryMapper = Mappers
      .getMapper(LoanLongTermHistoryMapper.class);
  private final LoanLongTermRepository loanLongTermRepository;
  private final LoanLongTermHistoryRepository loanLongTermHistoryRepository;

  @Override
  public List<LoanLongTerm> getUserBaseInfoFromResponse(UserBaseResponse userBaseResponse) {

    return ((ListLoanLongTermsResponse) userBaseResponse).getLongTermList();
  }

  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, List<LoanLongTerm> loanLongTerms) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    // FIXME: 신정원 종합포털 문의결과에 따라 로직 수정해야 함
    //  현재 로직: 리스트가 동일하면 DB 업데이트 없고, 다르면 모두 삭제 후 추가한다.
    List<LoanLongTerm> existingLoanLongTerms = loanLongTermRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream().map(loanLongTermMapper::entityToDto).collect(Collectors.toList());

    if (ObjectComparator.isSameListIgnoreOrder(loanLongTerms, existingLoanLongTerms)) {
      return;
    }

    loanLongTermRepository.deleteByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId);

    List<LoanLongTermEntity> loanLongTermEntities = new ArrayList<>();
    List<LoanLongTermHistoryEntity> loanLongTermHistoryEntities = new ArrayList<>();
    for (int i = 0; i < loanLongTerms.size(); i++) {
      LoanLongTerm loanLongTerm = loanLongTerms.get(i);
      LoanLongTermEntity loanLongTermEntity = loanLongTermMapper.dtoToEntity(loanLongTerm);
      loanLongTermEntity.setSyncedAt(syncedAt);
      loanLongTermEntity.setBanksaladUserId(banksaladUserId);
      loanLongTermEntity.setOrganizationId(organizationId);
      loanLongTermEntity.setLoanLongTermNo(i);
      loanLongTermEntity.setCreatedBy(String.valueOf(banksaladUserId));
      loanLongTermEntity.setUpdatedBy(String.valueOf(banksaladUserId));

      loanLongTermEntities.add(loanLongTermEntity);
      loanLongTermHistoryEntities.add(loanLongTermHistoryMapper.toHistoryEntity(loanLongTermEntity));
    }
    loanLongTermRepository.saveAll(loanLongTermEntities);
    loanLongTermHistoryRepository.saveAll(loanLongTermHistoryEntities);
  }
}
