package com.banksalad.collectmydata.insu.common.db.repository;

import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailHistoryEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanDetailHistoryRepository extends JpaRepository<LoanDetailHistoryEntity, Long> {

}
