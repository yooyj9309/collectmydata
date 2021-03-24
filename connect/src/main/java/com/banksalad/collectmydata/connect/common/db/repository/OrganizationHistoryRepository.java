package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.OrganizationHistoryEntity;

public interface OrganizationHistoryRepository extends JpaRepository<OrganizationHistoryEntity, Long> {

}
