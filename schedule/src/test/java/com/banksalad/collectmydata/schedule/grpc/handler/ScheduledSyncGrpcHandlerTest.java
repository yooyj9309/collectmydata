package com.banksalad.collectmydata.schedule.grpc.handler;

import org.springframework.boot.test.context.SpringBootTest;

import com.banksalad.collectmydata.schedule.sync.service.ScheduledSyncService;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.HealthCheckRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.HealthCheckResponse;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.RegisterScheduledSyncRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.RegisterScheduledSyncResponse;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.UnregisterScheduledSyncRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.UnregisterScheduledSyncResponse;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

@SpringBootTest
@DisplayName("ScheduledSyncGrpcHandler Test")
class ScheduledSyncGrpcHandlerTest {

  @InjectMocks
  private ScheduledSyncGrpcHandler scheduledSyncGrpcHandler;

  @Mock
  private ScheduledSyncService scheduledSyncService;

  @Test
  @DisplayName("gRPC Call Success Test : registerScheduledSync")
  void givenRegisterScheduledSyncRequest_whenCalledRegisterScheduledSyncMethod_thenSuccess() {
    // Given
    RegisterScheduledSyncRequest registerScheduledSyncRequest = getRegisterScheduledSyncRequest();
    StreamRecorder<RegisterScheduledSyncResponse> streamRecorder = StreamRecorder.create();

    // When
    scheduledSyncGrpcHandler.registerScheduledSync(registerScheduledSyncRequest, streamRecorder);

    // Then
    assertNull(streamRecorder.getError());
    then(scheduledSyncService).should(times(1)).register(any());
  }

  @Test
  @DisplayName("gRPC Call Fail Test : registerScheduledSync")
  void givenScheduledSyncSchedulerThrowException_whenCalledRegisterScheduledSyncMethod_thenGetError() {
    // Given
    RegisterScheduledSyncRequest registerScheduledSyncRequest = getRegisterScheduledSyncRequest();
    StreamRecorder<RegisterScheduledSyncResponse> streamRecorder = StreamRecorder.create();
    willThrow(new RuntimeException("Test Message")).given(scheduledSyncService).register(any());

    // When
    scheduledSyncGrpcHandler.registerScheduledSync(registerScheduledSyncRequest, streamRecorder);

    // Then
    assertNotNull(streamRecorder.getError());
    assertEquals(RuntimeException.class, streamRecorder.getError().getClass());
    assertEquals("Test Message", streamRecorder.getError().getMessage());
  }

  @Test
  @DisplayName("gRPC Call Success Test : unregisterScheduledSync")
  void givenUnregisterScheduledSyncRequest_whenCalledUnregisterScheduledSync_thenResponseSuccess() {
    // Given
    UnregisterScheduledSyncRequest registerScheduledSyncRequest = getUnregisterScheduledSyncRequest();
    StreamRecorder<UnregisterScheduledSyncResponse> streamRecorder = StreamRecorder.create();

    // When
    scheduledSyncGrpcHandler.unregisterScheduledSync(registerScheduledSyncRequest, streamRecorder);

    // Then
    assertNull(streamRecorder.getError());
    then(scheduledSyncService).should(times(1)).unregister(any());
  }

  @Test
  @DisplayName("gRPC Call Fail Test : unregisterScheduledSync")
  void givenScheduledSyncSchedulerThrowException_whenCalledUnregisterScheduledSyncMethod_thenGetError() {
    // Given
    UnregisterScheduledSyncRequest unregisterScheduledSyncRequest = getUnregisterScheduledSyncRequest();
    StreamRecorder<UnregisterScheduledSyncResponse> streamRecorder = StreamRecorder.create();
    willThrow(new RuntimeException("Test Message")).given(scheduledSyncService).unregister(any());

    // When
    scheduledSyncGrpcHandler.unregisterScheduledSync(unregisterScheduledSyncRequest, streamRecorder);

    // Then
    assertNotNull(streamRecorder.getError());
    assertEquals(RuntimeException.class, streamRecorder.getError().getClass());
    assertEquals("Test Message", streamRecorder.getError().getMessage());
  }

  @Test
  @DisplayName("gRPC Call Success Test : healthCheck")
  void givenHealthCheckRequest_whenCalledHealthCheckMethod_thenResponseSuccess() {
    // Given
    HealthCheckRequest healthCheckRequest = getHealthCheckRequest();
    StreamRecorder<HealthCheckResponse> streamRecorder = StreamRecorder.create();

    // When
    scheduledSyncGrpcHandler.healthCheck(healthCheckRequest, streamRecorder);

    // Then
    assertNull(streamRecorder.getError());
  }

  private RegisterScheduledSyncRequest getRegisterScheduledSyncRequest() {
    return RegisterScheduledSyncRequest.newBuilder()
        .setBanksaladUserId("1234")
        .setSector("finance")
        .setIndustry("card")
        .setOrganizationId("shinhancard")
        .build();
  }

  private UnregisterScheduledSyncRequest getUnregisterScheduledSyncRequest() {
    return UnregisterScheduledSyncRequest.newBuilder()
        .setBanksaladUserId("1234")
        .setSector("finance")
        .setIndustry("card")
        .setOrganizationId("shinhancard")
        .build();
  }

  private HealthCheckRequest getHealthCheckRequest() {
    return HealthCheckRequest.newBuilder().build();
  }
}
