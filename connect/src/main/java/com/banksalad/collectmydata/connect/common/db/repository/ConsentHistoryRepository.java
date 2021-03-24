package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.ConsentHistoryEntity;

public interface ConsentHistoryRepository extends JpaRepository<ConsentHistoryEntity, Long> {

}
