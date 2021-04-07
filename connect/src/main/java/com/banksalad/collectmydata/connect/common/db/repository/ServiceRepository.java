package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.ServiceEntity;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

  Optional<ServiceEntity> findByOrgCodeAndClientId(String orgCode, String clientId);
}
