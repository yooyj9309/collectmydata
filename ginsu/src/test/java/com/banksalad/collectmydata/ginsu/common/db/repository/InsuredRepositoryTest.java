package com.banksalad.collectmydata.ginsu.common.db.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Transactional
@SpringBootTest
class InsuredRepositoryTest {

  @Autowired
  private InsuredRepository insuredRepository;

  @DisplayName("DELETE 쿼리 1번 실행되는지 테스트")
  @Test
  void deleteQueryTest() {
    String insuNum = "insuNum";
    String organizationId = "organizationId";
    Long banksaladUserId = 1L;

    IntStream.range(0, 100)
        .forEach(i -> {
          InsuredEntity insuredEntity = InsuredEntity.builder()
              .organizationId(organizationId)
              .banksaladUserId(banksaladUserId)
              .insuNum(insuNum)
              .insuredName("1")
              .insuredNo((short) i)
              .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
              .build();

          insuredRepository.save(insuredEntity);
        });

    insuredRepository.deleteInsuredByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId,
        organizationId, insuNum);
  }
}
