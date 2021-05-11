package com.banksalad.collectmydata.card.card.userbase;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.ListRevolvingsResponse;
import com.banksalad.collectmydata.card.card.dto.Revolving;
import com.banksalad.collectmydata.card.common.db.entity.RevolvingEntity;
import com.banksalad.collectmydata.card.common.db.entity.RevolvingHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.RevolvingHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.RevolvingRepository;
import com.banksalad.collectmydata.card.common.mapper.RevolvingHistoryMapper;
import com.banksalad.collectmydata.card.common.mapper.RevolvingMapper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class RevolvingResponseHelper implements UserBaseResponseHelper<List<Revolving>> {

  private final RevolvingMapper revolvingMapper = Mappers.getMapper(RevolvingMapper.class);
  private final RevolvingHistoryMapper revolvingHistoryMapper = Mappers.getMapper(RevolvingHistoryMapper.class);
  private final RevolvingRepository revolvingRepository;
  private final RevolvingHistoryRepository revolvingHistoryRepository;

  @Override
  public List<Revolving> getUserBaseInfoFromResponse(UserBaseResponse userBaseResponse) {

    final ListRevolvingsResponse listRevolvingsResponse = (ListRevolvingsResponse) userBaseResponse;
    final int revolvingMonth = listRevolvingsResponse.getRevolvingMonth();
    final List<Revolving> revolvings = listRevolvingsResponse.getRevolvingList();

    for (Revolving revolving : revolvings) {
      revolving.setRevolvingMonth(revolvingMonth);
    }

    return revolvings;
  }

  /**
   * 6.3.10 DB 저장로직 : 월 별로 delete & insert
   * @author hyunjun
   */
  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, List<Revolving> revolvings) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();
    /**
     * 6.3.9에서 true인 경우만 6.3.10을 조회하니 revolvings는 항상 존재.
     * @author hyunjun
     */
    final int revolvingMonth = revolvings.get(0).getRevolvingMonth();

    /* 월별로 delete */
    revolvingRepository
        .deleteAllByBanksaladUserIdAndOrganizationIdAndRevolvingMonthInQuery(banksaladUserId, organizationId,
            revolvingMonth);

    AtomicInteger atomicInteger = new AtomicInteger(1);

    for (Revolving revolving : revolvings) {

      RevolvingEntity revolvingEntity = revolvingMapper.dtoToEntity(revolving);
      revolvingEntity.setSyncedAt(syncedAt);
      revolvingEntity.setBanksaladUserId(banksaladUserId);
      revolvingEntity.setOrganizationId(organizationId);
      revolvingEntity.setRevolvingNo((short) atomicInteger.getAndIncrement());
      revolvingEntity.setReqDate(revolving.getReqDate());
      revolvingEntity.setCreatedBy(String.valueOf(banksaladUserId));
      revolvingEntity.setUpdatedBy(String.valueOf(banksaladUserId));
      revolvingEntity.setConsentId(executionContext.getConsentId());
      revolvingEntity.setSyncRequestId(executionContext.getSyncRequestId());

      revolvingRepository.save(revolvingEntity);
      revolvingHistoryRepository
          .save(revolvingHistoryMapper.toHistoryEntity(revolvingEntity, RevolvingHistoryEntity.builder()
              .build()));
    }
  }
}
