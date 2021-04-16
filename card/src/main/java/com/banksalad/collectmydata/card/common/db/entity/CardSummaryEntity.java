package com.banksalad.collectmydata.card.common.db.entity;

import com.banksalad.collectmydata.finance.common.db.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "card_summary")
public class CardSummaryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime syncedAt;

  @Column(nullable = false)
  private Long banksaladUserId;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false)
  private String cardId;

  @Column(nullable = false, name = "card_num_encrypted")
  private String cardNum;

  @Column(nullable = false, name = "is_consent", columnDefinition = "BIT", length = 1)
  private Boolean consent;

  @Column(nullable = false)
  private String cardName;

  @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
  private Integer cardMember;

  private Long searchTimestamp;

  private String responseCode;

  private LocalDateTime approvalDomesticTransactionSyncedAt;

  private String approvalDomesticTransactionResponseCode;

  private LocalDateTime approvalOverseasTransactionSyncedAt;

  private String approvalOverseasTransactionResponseCode;

}
