package com.banksalad.collectmydata.card.card.userbase;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.ListPaymentsResponse;
import com.banksalad.collectmydata.card.card.dto.Payment;
import com.banksalad.collectmydata.card.common.db.entity.PaymentEntity;
import com.banksalad.collectmydata.card.common.db.repository.PaymentRepository;
import com.banksalad.collectmydata.card.common.mapper.PaymentMapper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class PaymentResponseHelper implements UserBaseResponseHelper<List<Payment>> {

  private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

  private final PaymentRepository paymentRepository;

  @Override
  public List<Payment> getUserBaseInfoFromResponse(UserBaseResponse userBaseResponse) {
    return ((ListPaymentsResponse) userBaseResponse).getPayList();
  }

  /**
   * 6.3.6은 seqno, pay_due_date로 유니크를 잡을 수 있으니 upsert.
   * @author hyunjun
   */
  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, List<Payment> payments) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    for (Payment payment : payments) {
      PaymentEntity paymentEntity = paymentMapper.dtoToEntity(payment);
      paymentEntity.setSyncedAt(syncedAt);
      paymentEntity.setBanksaladUserId(banksaladUserId);
      paymentEntity.setOrganizationId(organizationId);
      paymentEntity.setCreatedBy(String.valueOf(banksaladUserId));
      paymentEntity.setUpdatedBy(String.valueOf(banksaladUserId));
      paymentEntity.setConsentId(executionContext.getConsentId());
      paymentEntity.setSyncRequestId(executionContext.getSyncRequestId());

      PaymentEntity existingPaymentEntity = paymentRepository
          .findByBanksaladUserIdAndOrganizationIdAndSeqnoAndPayDueDate(banksaladUserId, organizationId,
              payment.getSeqno(), payment.getPayDueDate())
          .orElse(null);

      if (existingPaymentEntity != null) {
        paymentEntity.setId(existingPaymentEntity.getId());
        paymentEntity.setCreatedBy(existingPaymentEntity.getCreatedBy());
        paymentEntity.setCreatedAt(existingPaymentEntity.getCreatedAt());
      }

      /* update if entity has changed */
      if (!ObjectComparator.isSame(paymentEntity, existingPaymentEntity, ENTITY_EXCLUDE_FIELD)) {
        paymentRepository.save(paymentEntity);
      }
    }
  }
}
