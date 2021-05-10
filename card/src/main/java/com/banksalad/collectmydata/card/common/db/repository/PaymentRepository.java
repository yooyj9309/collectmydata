package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.PaymentEntity;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

  Optional<PaymentEntity> findByBanksaladUserIdAndOrganizationIdAndSeqnoAndPayDueDate(long banksaladUserId,
      String organizationId, String seqno, String payDueDate);

  List<PaymentEntity> findAllByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
