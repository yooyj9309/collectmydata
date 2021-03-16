package com.banksalad.collectmydata.telecom.common.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// TODO: Comment in below @AllArgsConstructor after adding responses.
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TelecomApiResponse {
  // TODO: add List<TelecomBill>, List<TelecomTransaction>, List<TelecomPaidTransaction>

}
