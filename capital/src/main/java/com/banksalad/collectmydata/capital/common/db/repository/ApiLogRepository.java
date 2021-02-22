package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.ApiLogEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ApiLogRepository extends JpaRepository<ApiLogEntity, Long> {

  Optional<ApiLogEntity> findBySyncRequestIdAndExecutionRequestIdAndApiRequestIdAndCreatedAtBetween(
      String syncRequestId,
      String executionRequestId,
      String apiRequestId,
      LocalDateTime minusDays,
      LocalDateTime plusDays
  );
}
