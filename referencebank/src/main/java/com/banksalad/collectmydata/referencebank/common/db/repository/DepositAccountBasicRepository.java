package com.banksalad.collectmydata.referencebank.common.db.repository;

import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountBasicEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepositAccountBasicRepository extends JpaRepository<DepositAccountBasicEntity, Long> {

  Optional<DepositAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      Long banksaladUserId, String organizationId, String accountNum, String seqno);

  List<DepositAccountBasicEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
