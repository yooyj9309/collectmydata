package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermHistoryEntity;

public interface LoanShortTermHistoryRepository extends JpaRepository<LoanShortTermHistoryEntity, Long> {

}
