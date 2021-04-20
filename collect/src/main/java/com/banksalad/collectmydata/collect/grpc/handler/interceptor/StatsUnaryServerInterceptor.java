package com.banksalad.collectmydata.collect.grpc.handler.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class StatsUnaryServerInterceptor implements ServerInterceptor {

  private static final String NO_CALLER = "NO_CALLER";
  private static final String HEADER_BANKSALAD_CALLER = "HEADER_BANKSALAD_CALLER-Caller";
  private static final String GRPC_ALL_COUNT_MEASUREMENT_SUFFIX = "grpc.endpoint.all.count";
  private static final String GRPC_ALL_TIMING_MEASUREMENT_SUFFIX = "grpc.endpoint.all.timing";

  private static final String TAG_METHOD = "method";
  private static final String TAG_CALLER = "caller";
  private static final String TAG_CODE = "code";

  private final MeterRegistry meterRegistry;

  public StatsUnaryServerInterceptor(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
      ServerCallHandler<ReqT, RespT> next) {

    Context context = Context.current();

    String caller =
        Optional.ofNullable(
            headers.get(Metadata.Key.of(HEADER_BANKSALAD_CALLER, Metadata.ASCII_STRING_MARSHALLER))
        ).orElse(NO_CALLER);

    String method = call.getMethodDescriptor().getFullMethodName();

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    return Contexts.interceptCall(
        context,
        new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
          @Override
          public void close(Status status, Metadata metadata) {
            stopWatch.stop();

            Tags tags =
                Tags.of(TAG_METHOD, method)
                    .and(TAG_CALLER, caller)
                    .and(TAG_CODE, status.getCode().name());

            meterRegistry.counter(GRPC_ALL_COUNT_MEASUREMENT_SUFFIX, tags).increment();
            meterRegistry.timer(GRPC_ALL_TIMING_MEASUREMENT_SUFFIX, tags)
                .record(stopWatch.getTotalTimeMillis(), TimeUnit.MILLISECONDS);

            log.debug("gRPC intercept: caller : {} , tags : {}, elapsed: {}", caller, tags,
                stopWatch.getTotalTimeMillis());

            super.close(status, metadata);
          }
        },
        headers,
        next);
  }
}
