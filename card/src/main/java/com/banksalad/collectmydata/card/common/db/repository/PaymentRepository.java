package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.card.common.db.entity.PaymentEntity;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

  Optional<PaymentEntity> findByBanksaladUserIdAndOrganizationIdAndSeqnoAndPayDueDate(long banksaladUserId,
      String organizationId, String seqno, String payDueDate);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete from PaymentEntity p where p.banksaladUserId = ?1 and p.organizationId = ?2")
  void deleteAllByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

  List<PaymentEntity> findAllByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
