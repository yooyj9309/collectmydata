package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;

import java.util.List;
import java.util.Optional;

public interface LoanAccountBasicRepository extends JpaRepository<LoanAccountBasicEntity, Long> {

  Optional<LoanAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      Long banksaladUserId,
      String organizationId,
      String accountNum,
      String seqNo);

  List<LoanAccountBasicEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
