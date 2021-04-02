package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.ApprovalDomesticEntity;
import com.banksalad.collectmydata.card.common.db.entity.ApprovalOverseasEntity;

import java.util.Optional;

public interface ApprovalOverseasRepository extends JpaRepository<ApprovalOverseasEntity, Long> {

  Optional<ApprovalOverseasEntity> findByApprovalYearMonthAndBanksaladUserIdAndOrganizationIdAndCardIdAndApprovedNumAndStatus(
      Integer approvalYearMonth, Long banksaladUserId, String organizationId, String cardId, String approvedNum, String status);
}
