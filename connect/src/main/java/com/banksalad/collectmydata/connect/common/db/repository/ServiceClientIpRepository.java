package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.banksalad.collectmydata.connect.common.db.entity.ServiceClientIpEntity;

public interface ServiceClientIpRepository extends JpaRepository<ServiceClientIpEntity, Long> {

  @Modifying
  @Query("delete from ServiceClientIpEntity e where e.orgCode = ?1 and e.serviceId = ?2")
  void deleteAllByOrgCodeAndServiceId(String orgCode, Long serviceId);
}
