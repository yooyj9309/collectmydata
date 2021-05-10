package com.banksalad.collectmydata.card.publishment.transaction;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.card.card.dto.ApprovalDomestic;
import com.banksalad.collectmydata.card.card.dto.ApprovalOverseas;
import com.banksalad.collectmydata.card.common.db.repository.ApprovalDomesticRepository;
import com.banksalad.collectmydata.card.common.db.repository.ApprovalOverseasRepository;
import com.banksalad.collectmydata.card.common.mapper.ApprovalDomesticMapper;
import com.banksalad.collectmydata.card.common.mapper.ApprovalOverseasMapper;
import com.banksalad.collectmydata.card.publishment.transaction.dto.ApprovalDomesticPublishment;
import com.banksalad.collectmydata.card.publishment.transaction.dto.ApprovalOverseasPublishment;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardTransactionPublishServiceImpl implements CardTransactionPublishService {

  private final ApprovalDomesticRepository approvalDomesticRepository;
  private final ApprovalDomesticMapper approvalDomesticMapper = Mappers.getMapper(ApprovalDomesticMapper.class);

  private final ApprovalOverseasRepository approvalOverseasRepository;
  private final ApprovalOverseasMapper approvalOverseasMapper = Mappers.getMapper(ApprovalOverseasMapper.class);

  @Override
  public List<ApprovalDomesticPublishment> getCardApprovalDomesticResponse(long banksaladUserId, String organizationId,
      String cardId, LocalDateTime createdAt, int limit) {

    return approvalDomesticRepository
        .findAllByBanksaladUserIdAndOrganizationIdAndCardIdAndCreatedAtAfter(
            banksaladUserId, organizationId, cardId, createdAt, PageRequest.of(0, limit)).stream()
        .map(approvalDomesticMapper::entityToPublishmentDto).collect(
            Collectors.toList());
  }

  @Override
  public List<ApprovalOverseasPublishment> getCardApprovalOverseasResponses(long banksaladUserId, String organizationId,
      String cardId, LocalDateTime createdAt, int limit) {

    return approvalOverseasRepository.findAllByBanksaladUserIdAndOrganizationIdAndCardIdAndCreatedAtAfter(
        banksaladUserId, organizationId, cardId, createdAt, PageRequest.of(0, limit)
    ).stream().map(approvalOverseasMapper::entityToPublishmentDto).collect(Collectors.toList());
  }
}
