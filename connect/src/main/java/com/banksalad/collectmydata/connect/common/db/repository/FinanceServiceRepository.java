package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.FinanceServiceEntity;

import java.util.Optional;

public interface FinanceServiceRepository extends JpaRepository<FinanceServiceEntity, Long> {

  Optional<FinanceServiceEntity> findByOrganizationId(String organizationId);
}
