package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.InvestAccountProductEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface InvestAccountProductRepository extends JpaRepository<InvestAccountProductEntity, Long> {

  List<InvestAccountProductEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndUpdatedAtGreaterThan(
      Long banksaladUserId, String organizationId, String accountNum, LocalDateTime updateAt);
}
