package com.banksalad.collectmydata.card.loan;

import org.springframework.stereotype.Component;

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
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

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

  /**
   * 6.3.11 DB 저장로직 : upsert
   * @author hyunjun
   */
  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, List<LoanShortTerm> loanShortTerms) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    AtomicInteger atomicInteger = new AtomicInteger(1);

    for (LoanShortTerm loanShortTerm : loanShortTerms) {

      LoanShortTermEntity loanShortTermEntity = loanShortTermMapper.dtoToEntity(loanShortTerm);
      loanShortTermEntity.setSyncedAt(syncedAt);
      loanShortTermEntity.setBanksaladUserId(banksaladUserId);
      loanShortTermEntity.setOrganizationId(organizationId);
      loanShortTermEntity.setLoanShortTermNo((short) atomicInteger.getAndIncrement());
      loanShortTermEntity.setCreatedBy(String.valueOf(banksaladUserId));
      loanShortTermEntity.setUpdatedBy(String.valueOf(banksaladUserId));
      loanShortTermEntity.setConsentId(executionContext.getConsentId());
      loanShortTermEntity.setSyncRequestId(executionContext.getSyncRequestId());

      LoanShortTermEntity existingEntity = loanShortTermRepository
          .findByBanksaladUserIdAndOrganizationIdAndLoanDtime(banksaladUserId, organizationId,
              loanShortTerm.getLoanDtime()).orElse(null);

      if (existingEntity != null) {
        loanShortTermEntity.setId(existingEntity.getId());
        loanShortTermEntity.setCreatedBy(existingEntity.getCreatedBy());
        loanShortTermEntity.setCreatedAt(existingEntity.getCreatedAt());
      }

      /* update if entity has changed */
      if (!ObjectComparator.isSame(existingEntity, loanShortTermEntity, ENTITY_EXCLUDE_FIELD)) {
        loanShortTermRepository.save(loanShortTermEntity);
        loanShortTermHistoryRepository
            .save(loanShortTermHistoryMapper.toHistoryEntity(loanShortTermEntity, LoanShortTermHistoryEntity.builder()
                .build()));
      }
    }
  }
}
