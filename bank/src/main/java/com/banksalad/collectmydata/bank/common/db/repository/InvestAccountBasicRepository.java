package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicEntity;

import java.util.List;
import java.util.Optional;

public interface InvestAccountBasicRepository extends JpaRepository<InvestAccountBasicEntity, Long> {

  Optional<InvestAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId,
      String organizationId, String accountNum, String seqno);
}
