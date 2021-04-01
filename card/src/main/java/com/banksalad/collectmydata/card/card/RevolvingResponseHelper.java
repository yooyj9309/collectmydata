package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.dto.ListRevolvingsResponse;
import com.banksalad.collectmydata.card.card.dto.Revolving;
import com.banksalad.collectmydata.card.common.db.entity.RevolvingEntity;
import com.banksalad.collectmydata.card.common.db.entity.RevolvingHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.RevolvingHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.RevolvingRepository;
import com.banksalad.collectmydata.card.common.mapper.RevolvingHistoryMapper;
import com.banksalad.collectmydata.card.common.mapper.RevolvingMapper;
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

  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, List<Revolving> revolvings) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    // FIXME: 신정원 종합포털 문의결과에 따라 로직 수정해야 함
    //  현재 로직: 리스트가 동일하면 DB 업데이트 없고, 다르면 모두 삭제 후 추가한다.
    List<Revolving> existingRevolvings = revolvingRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream().map(revolvingMapper::entityToDto).collect(Collectors.toList());

    if (ObjectComparator.isSameListIgnoreOrder(revolvings, existingRevolvings)) {
      return;
    }

    revolvingRepository.deleteByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId);

    List<RevolvingEntity> revolvingEntities = new ArrayList<>();
    List<RevolvingHistoryEntity> revolvingHistoryEntities = new ArrayList<>();
    for (int i = 0; i < revolvings.size(); i++) {
      Revolving revolving = revolvings.get(i);
      RevolvingEntity revolvingEntity = revolvingMapper.dtoToEntity(revolving);
      revolvingEntity.setSyncedAt(syncedAt);
      revolvingEntity.setBanksaladUserId(banksaladUserId);
      revolvingEntity.setOrganizationId(organizationId);
      revolvingEntity.setRevolvingNo(i);
      revolvingEntity.setCreatedBy(String.valueOf(banksaladUserId));
      revolvingEntity.setUpdatedBy(String.valueOf(banksaladUserId));

      revolvingEntities.add(revolvingEntity);
      revolvingHistoryEntities.add(revolvingHistoryMapper.toHistoryEntity(revolvingEntity));
    }
    revolvingRepository.saveAll(revolvingEntities);
    revolvingHistoryRepository.saveAll(revolvingHistoryEntities);
  }
}
