package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.ServiceClientIpEntity;

import java.util.Optional;

public interface ServiceClientIpRepository extends JpaRepository<ServiceClientIpEntity, Long> {

  Optional<ServiceClientIpEntity> findByServiceIdAndClientIp(Long serviceId, String clientIp);
}
