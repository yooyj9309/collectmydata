package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.FinanceServiceClientIpEntity;

import java.util.Optional;

public interface FinanceServiceClientIpRepository extends JpaRepository<FinanceServiceClientIpEntity, Long> {

  Optional<FinanceServiceClientIpEntity> findByServiceIdAndClientIp(Long serviceId, String clientIp);
}
