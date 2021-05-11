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

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;

@Transactional
@SpringBootTest
class InsuredRepositoryTest {

  @Autowired
  private InsuredRepository insuredRepository;

  @DisplayName("DELETE 쿼리 1번 실행되는지 테스트")
  @Test
  void deleteQueryTest() {
    String insuNum = "insuNum";

    IntStream.range(0, 100)
        .forEach(i -> {
          InsuredEntity insuredEntity = InsuredEntity.builder()
              .organizationId(ORGANIZATION_ID)
              .banksaladUserId(BANKSALAD_USER_ID)
              .insuNum(insuNum)
              .insuredName("1")
              .insuredNo((short) i)
              .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
              .consentId(CONSENT_ID)
              .syncRequestId(SYNC_REQUEST_ID)
              .build();

          insuredRepository.save(insuredEntity);
        });

    insuredRepository.deleteInsuredByBanksaladUserIdAndOrganizationIdAndInsuNum(BANKSALAD_USER_ID,
        ORGANIZATION_ID, insuNum);
  }
}
