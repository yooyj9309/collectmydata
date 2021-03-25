package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.ginsu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.ginsu.insurance.dto.Insured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("보증보험 기본정보 매퍼 테스트")
class InsuranceBasicMapperTest {

  private InsuranceBasicMapper insuranceBasicMapper = Mappers.getMapper(InsuranceBasicMapper.class);

  @Test
  void dtoToEntityTest() {
    InsuranceBasic insuranceBasic = InsuranceBasic.builder()
        .issueDate("20210301")
        .expDate("20300415")
        .faceAmt(BigDecimal.valueOf(180000, 3))
        .insuredCount(1)
        .insuredList(
            List.of(
                Insured.builder()
                    .insuredName("피보험자명")
                    .build()
            )
        )
        .payAmt(BigDecimal.valueOf(300000000, 3))
        .payDue("02")
        .build();

    InsuranceBasicEntity insuranceBasicEntity = insuranceBasicMapper.dtoToEntity(insuranceBasic);

    assertAll(
        () -> assertEquals(insuranceBasic.getIssueDate(), insuranceBasicEntity.getIssueDate()),
        () -> assertEquals(insuranceBasic.getExpDate(), insuranceBasicEntity.getExpDate()),
        () -> assertEquals(insuranceBasic.getFaceAmt(), insuranceBasicEntity.getFaceAmt()),
        () -> assertEquals(insuranceBasic.getPayAmt(), insuranceBasicEntity.getPayAmt()),
        () -> assertEquals(insuranceBasic.getPayDue(), insuranceBasicEntity.getPayDue())
    );
  }
}
