package com.banksalad.collectmydata.card.card.bill;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.ListBillBasicResponse;
import com.banksalad.collectmydata.card.common.db.entity.BillEntity;
import com.banksalad.collectmydata.card.common.db.entity.BillHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.BillHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.BillRepository;
import com.banksalad.collectmydata.card.common.mapper.BillHistoryMapper;
import com.banksalad.collectmydata.card.common.mapper.BillMapper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.bill.BillResponseHelper;
import com.banksalad.collectmydata.finance.api.bill.dto.BillResponse;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class BillBasicResponseHelper implements BillResponseHelper<BillBasic> {

  private final BillRepository billRepository;
  private final BillHistoryRepository billHistoryRepository;

  private final BillMapper billMapper = Mappers.getMapper(BillMapper.class);
  private final BillHistoryMapper billHistoryMapper = Mappers.getMapper(BillHistoryMapper.class);

  @Override
  public List<BillBasic> getBillsFromResponse(BillResponse billResponse) {
    return ((ListBillBasicResponse) billResponse).getBillBasics();
  }

  @Override
  public void saveBills(ExecutionContext executionContext, List<BillBasic> billBasics) {
    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    /* delete & insert */
    billBasics.forEach(billBasic -> {
          /** delete
           * cardType, seqno까지 지정해 지우는 이유는 pagination이기 때문에 이전 page로 넘어온 데이터를 지우면 안되고 이번에 온 데이터만 지워야한다.
           * @author hyunjun
           */
          billRepository
              .deleteAllByBanksaladUserIdAndOrganizationIdAndChargeMonthAndCardTypeAndSeqnoInQuery(banksaladUserId,
                  organizationId, billBasic.getChargeMonth(), billBasic.getCardType(), billBasic.getSeqno());
        }
    );

    billBasics.forEach(billBasic -> {
      BillEntity billEntity = billMapper.dtoToEntity(billBasic);
      billEntity.setSyncedAt(syncedAt);
      billEntity.setBanksaladUserId(banksaladUserId);
      billEntity.setOrganizationId(organizationId);
      billEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
      billEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));
      billEntity.setConsentId(executionContext.getConsentId());
      billEntity.setSyncRequestId(executionContext.getSyncRequestId());

      /* insert */
      billRepository.save(billEntity);
      billHistoryRepository.save(billHistoryMapper.toHistoryEntity(billEntity, BillHistoryEntity.builder().build()));
    });
  }
}
