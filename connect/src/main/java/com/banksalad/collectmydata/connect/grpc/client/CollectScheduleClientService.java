package com.banksalad.collectmydata.connect.grpc.client;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.connect.common.dto.Consent;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.RegisterScheduledSyncRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.UnregisterScheduledSyncRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectscheduleGrpc.CollectscheduleBlockingStub;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectScheduleClientService {

  private final CollectscheduleBlockingStub collectscheduleBlockingStub;

  public void registerScheduledSync(long banksaladUserId, Organization organization, Consent consent) {
    RegisterScheduledSyncRequest request = RegisterScheduledSyncRequest.newBuilder()
        .setBanksaladUserId(String.valueOf(banksaladUserId))
        .setSector(organization.getSector())
        .setIndustry(organization.getIndustry())
        .setOrganizationId(organization.getOrganizationId())
        .setConsentId(consent.getConsentId())
        .setCycle(consent.getCycle())
        .setEndDate(consent.getEndDate())
        .build();

    collectscheduleBlockingStub.registerScheduledSync(request);
  }

  public void unregisterScheduledSync(long banksaladUserId, Organization organization, Consent consent) {
    UnregisterScheduledSyncRequest request = UnregisterScheduledSyncRequest.newBuilder()
        .setBanksaladUserId(String.valueOf(banksaladUserId))
        .setSector(organization.getSector())
        .setIndustry(organization.getIndustry())
        .setOrganizationId(organization.getOrganizationId())
        .setConsentId(consent.getConsentId())
        .setCycle(consent.getCycle())
        .setEndDate(consent.getEndDate())
        .build();

    collectscheduleBlockingStub.unregisterScheduledSync(request);
  }
}
