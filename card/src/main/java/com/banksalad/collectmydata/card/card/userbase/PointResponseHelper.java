package com.banksalad.collectmydata.card.card.userbase;

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
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

  /** 6.3.3 포인트 조회는 유니크를 잡기 어려워 delete & insert로 변경
   * @author hyunjun
   */
  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, List<Point> points) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    /* delete */
    pointRepository.deleteAllByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId);

    AtomicInteger atomicInteger = new AtomicInteger(1);

    /* insert */
    for (Point point : points) {
      PointEntity pointEntity = pointMapper.dtoToEntity(point);
      pointEntity.setSyncedAt(syncedAt);
      pointEntity.setBanksaladUserId(banksaladUserId);
      pointEntity.setOrganizationId(organizationId);
      pointEntity.setCreatedBy(String.valueOf(banksaladUserId));
      pointEntity.setUpdatedBy(String.valueOf(banksaladUserId));
      pointEntity.setConsentId(executionContext.getConsentId());
      pointEntity.setSyncRequestId(executionContext.getSyncRequestId());
      pointEntity.setPointNo((short) atomicInteger.getAndIncrement());

      pointRepository.save(pointEntity);
      pointHistoryRepository
          .save(pointHistoryMapper.toHistoryEntity(pointEntity, PointHistoryEntity.builder().build()));
    }
  }
}
