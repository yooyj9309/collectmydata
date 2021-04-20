package com.banksalad.collectmydata.card.card;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.ListPointsResponse;
import com.banksalad.collectmydata.card.card.dto.Point;
import com.banksalad.collectmydata.card.common.db.entity.PointEntity;
import com.banksalad.collectmydata.card.common.db.entity.PointHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.PointHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.PointRepository;
import com.banksalad.collectmydata.card.common.mapper.PointHistoryMapper;
import com.banksalad.collectmydata.card.common.mapper.PointMapper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class PointResponseHelper implements UserBaseResponseHelper<List<Point>> {

  private final PointMapper pointMapper = Mappers.getMapper(PointMapper.class);
  private final PointHistoryMapper pointHistoryMapper = Mappers.getMapper(PointHistoryMapper.class);
  private final PointRepository pointRepository;
  private final PointHistoryRepository pointHistoryRepository;

  @Override
  public List<Point> getUserBaseInfoFromResponse(UserBaseResponse userBaseResponse) {

    return ((ListPointsResponse) userBaseResponse).getPointList();
  }

  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, List<Point> points) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    List<PointEntity> pointEntities = new ArrayList<>();
    List<PointHistoryEntity> pointHistoryEntities = new ArrayList<>();

    int pointNo = 0;
    for (Point point : points) {
      PointEntity pointEntity = pointMapper.dtoToEntity(point);
      pointEntity.setSyncedAt(syncedAt);
      pointEntity.setBanksaladUserId(banksaladUserId);
      pointEntity.setOrganizationId(organizationId);
      pointEntity.setCreatedBy(String.valueOf(banksaladUserId));
      pointEntity.setUpdatedBy(String.valueOf(banksaladUserId));
      pointEntity.setPointNo((short) pointNo++);

      PointEntity existingPointEntity = pointRepository
          .findByBanksaladUserIdAndOrganizationIdAndPointName(banksaladUserId, organizationId, point.getPointName())
          .orElse(PointEntity.builder().build());
      existingPointEntity.setId(pointEntity.getId());
      if (ObjectComparator.isSame(pointEntity, existingPointEntity, ENTITY_EXCLUDE_FIELD)) {
        continue;
      }
      pointEntities.add(pointEntity);
      pointHistoryEntities.add(pointHistoryMapper.toHistoryEntity(pointEntity));
    }
    pointRepository.saveAll(pointEntities);
    pointHistoryRepository.saveAll(pointHistoryEntities);
  }
}
