package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.TransactionEntity;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

  Optional<TransactionEntity> findByBanksaladUserIdAndOrganizationIdAndSubKeyAndFobNameAndTransNumAndTransDtime(
      Long banksaladUserId, String organizationId, String subKey, String fobName, String transNum, String transDtime);
}
