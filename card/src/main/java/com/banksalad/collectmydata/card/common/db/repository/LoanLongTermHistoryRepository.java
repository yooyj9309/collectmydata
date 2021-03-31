package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermHistoryEntity;

public interface LoanLongTermHistoryRepository extends JpaRepository<LoanLongTermHistoryEntity, Long> {

}
