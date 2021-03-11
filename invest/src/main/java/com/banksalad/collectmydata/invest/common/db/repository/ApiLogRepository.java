package com.banksalad.collectmydata.invest.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.invest.common.db.entity.ApiLogEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApiLogRepository extends JpaRepository<ApiLogEntity, Long> {

  Optional<ApiLogEntity> findBySyncRequestIdAndExecutionRequestIdAndApiRequestIdAndCreatedAtBetween(
      String syncRequestId, String executionRequestId, String apiRequestId, LocalDateTime minusDays,
      LocalDateTime plusDays
  );

  List<ApiLogEntity> findBySyncRequestIdAndExecutionRequestIdAndCreatedAtBetween(String syncRequestId,
      String executionRequestId, LocalDateTime minusDays, LocalDateTime plusDays);
}
