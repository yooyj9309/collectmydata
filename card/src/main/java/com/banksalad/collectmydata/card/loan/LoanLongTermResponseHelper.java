package com.banksalad.collectmydata.card.loan;

import org.springframework.stereotype.Component;

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
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

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

  /**
   * 6.3.12 DB 저장로직 : upsert
   * @author hyunjun
   */
  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, List<LoanLongTerm> loanLongTerms) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    AtomicInteger atomicInteger = new AtomicInteger(1);

    for (LoanLongTerm loanLongTerm : loanLongTerms) {

      LoanLongTermEntity loanLongTermEntity = loanLongTermMapper.dtoToEntity(loanLongTerm);
      loanLongTermEntity.setSyncedAt(syncedAt);
      loanLongTermEntity.setBanksaladUserId(banksaladUserId);
      loanLongTermEntity.setOrganizationId(organizationId);
      loanLongTermEntity.setLoanLongTermNo((short) atomicInteger.getAndIncrement());
      loanLongTermEntity.setCreatedBy(String.valueOf(banksaladUserId));
      loanLongTermEntity.setUpdatedBy(String.valueOf(banksaladUserId));
      loanLongTermEntity.setConsentId(executionContext.getConsentId());
      loanLongTermEntity.setSyncRequestId(executionContext.getSyncRequestId());

      LoanLongTermEntity existingLoanLongTermEntity = loanLongTermRepository
          .findByBanksaladUserIdAndOrganizationIdAndLoanDtimeAndLoanCnt(banksaladUserId, organizationId,
              loanLongTerm.getLoanDtime(), loanLongTerm.getLoanCnt()).orElse(null);

      if (existingLoanLongTermEntity != null) {
        loanLongTermEntity.setId(existingLoanLongTermEntity.getId());
        loanLongTermEntity.setCreatedBy(existingLoanLongTermEntity.getCreatedBy());
        loanLongTermEntity.setCreatedAt(existingLoanLongTermEntity.getCreatedAt());
      }

      /* update if entity has changed */
      if (!ObjectComparator.isSame(existingLoanLongTermEntity, loanLongTermEntity, ENTITY_EXCLUDE_FIELD)) {
        loanLongTermRepository.save(loanLongTermEntity);
        loanLongTermHistoryRepository.save(
            loanLongTermHistoryMapper
                .toHistoryEntity(loanLongTermEntity, LoanLongTermHistoryEntity.builder().build()));
      }
    }
  }
}
