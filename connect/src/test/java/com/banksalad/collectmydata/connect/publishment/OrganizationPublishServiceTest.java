package com.banksalad.collectmydata.connect.publishment;

import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.publishment.organization.dto.OrganizationForFinance;
import com.banksalad.collectmydata.connect.publishment.organization.service.OrganizationPublishService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.banksalad.collectmydata.connect.common.ConnectConstant.DOMAIN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.INDUSTRY;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_GUID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_STATUS;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.SECTOR;
import static com.banksalad.collectmydata.connect.common.constant.OrganizationConstants.AUTH_URI;
import static java.lang.Boolean.FALSE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class OrganizationPublishServiceTest {

  @Autowired
  private OrganizationPublishService organizationPublishService;
  @Autowired
  private ConnectOrganizationRepository connectOrganizationRepository;

  @Test
  @DisplayName("전체 기관 목록 가져오기 for finance")
  void listFinanceOrganizations_success() {
    // Given
    ConnectOrganizationEntity connectOrganizationEntity = getConnectOrganizationEntity();
    connectOrganizationRepository.save(connectOrganizationEntity);

    // When
    List<OrganizationForFinance> organizationForFinances = organizationPublishService.listFinanceOrganizations();

    // Then
    assertThat(organizationForFinances.size()).isEqualTo(1);
    assertThat(organizationForFinances).usingRecursiveComparison().isEqualTo(List.of(getOrganizationForFinance()));
  }

  @Test
  @DisplayName("특정 사용자의 연결된 기관 목록 가져오기 for finance")
  void listConnectedFinanceOrganizations_success() {
    // Given
    // When
    // Then
  }

  private ConnectOrganizationEntity getConnectOrganizationEntity() {
    return ConnectOrganizationEntity.builder()
        .sector(SECTOR)
        .industry(INDUSTRY)
        .organizationId(ORGANIZATION_ID)
        .organizationGuid(ORGANIZATION_GUID)
        .orgCode(ORGANIZATION_CODE)
        .organizationStatus(ORGANIZATION_STATUS)
        .domain(DOMAIN)
        .deleted(FALSE)
        .build();
  }

  private OrganizationForFinance getOrganizationForFinance() {
    return OrganizationForFinance.builder()
        .organizationGuid(ORGANIZATION_GUID)
        .organizationId(ORGANIZATION_ID)
        .authUrl(DOMAIN + AUTH_URI)
        .build();
  }
}
