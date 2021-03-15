package com.banksalad.collectmydata.common.collect.executor;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Api.Transform;
import com.banksalad.collectmydata.common.collect.api.ApiResponseEntity;
import com.banksalad.collectmydata.common.collect.api.Pagination;
import com.banksalad.collectmydata.common.collect.apilog.ApiLogger;
import com.banksalad.collectmydata.common.collect.apilog.IdGenerator;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.ApiLog.Request;
import com.banksalad.collectmydata.common.collect.executor.ApiLog.Response;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;

import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.schibsted.spt.data.jslt.JsltException;
import com.schibsted.spt.data.jslt.Parser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CollectExecutorImpl implements CollectExecutor {

  private final static int PAGINATION_MAX = 100;

  private final TransferClient transferClient;
  private final IdGenerator idGenerator;
  private final ApiLogger apiLogger;

  private final ObjectMapper objectMapper;

  public CollectExecutorImpl(
      TransferClient transferClient,
      IdGenerator idGenerator,
      ApiLogger apiLogger
  ) {

    this.transferClient = transferClient;
    this.idGenerator = idGenerator;
    this.apiLogger = apiLogger;

    this.objectMapper = new ObjectMapper();
    configureObjectMapper(this.objectMapper);
  }

  private void configureObjectMapper(ObjectMapper objectMapper) {

    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
    objectMapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));

    objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
      @Override
      public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializers)
          throws IOException {
        if (!jsonGenerator.getOutputContext().inArray()) {
          jsonGenerator.writeNull();
        }
      }
    });
  }

  @Override
  public <T, R> ExecutionResponse<R> execute(ExecutionContext context, Execution execution,
      ExecutionRequest<T> executionRequest) {

    JsonNode requestJsonNode = objectMapper.convertValue(executionRequest.getRequest(), JsonNode.class);

    return exchange(context, execution,
        ExecutionRequest.builder()
            .headers(Optional.ofNullable(executionRequest.getHeaders()).orElse(Map.of()))
            .request(objectMapper.convertValue(requestJsonNode, executionRequest.getRequest().getClass()))
            .build());
  }


  private <T, R> ExecutionResponse<R> exchange(ExecutionContext context, Execution execution,
      ExecutionRequest<T> executionRequest) {

    String requestNextPage = getRequestNextPage(execution.getApi(), executionRequest);

    /* generate request id */
    String apiRequestId = idGenerator.generate();

    Map<String, String> transformedRequestHeader = transformRequestHeader(apiRequestId, execution, executionRequest);
    String transformedRequestBody = transformRequestBody(apiRequestId, execution, executionRequest);

    /* logging request */
    logRequest(context, apiRequestId, execution.getApi(), executionRequest, transformedRequestHeader,
        transformedRequestBody);

    /* transfer */
    ApiResponseEntity apiResponseEntity = transferClient.execute(
        context.getOrganizationHost(),
        buildUri(execution.getApi().getEndpoint(), transformedRequestBody),
        execution.getApi().getMethod(),
        transformedRequestHeader,
        transformedRequestBody
    );

    Map<String, String> standardResponseHeader = transformResponseHeader(apiRequestId, execution,
        apiResponseEntity.getHeaders());

    String standardResponseBody = transformResponseBody(apiRequestId, execution, apiResponseEntity.getBody());
    String responseNextPage = getResponseNextPage(execution.getApi(), standardResponseBody);

    /* logging response */
    logResponse(context, apiRequestId, execution.getApi(), apiResponseEntity, standardResponseHeader,
        standardResponseBody);

    try {
      Class<R> clazz = execution.getAs();
      R response = objectMapper.readValue(standardResponseBody, clazz);

      return ExecutionResponse.<R>builder()
          .httpStatusCode(apiResponseEntity.getHttpStatusCode())
          .headers(transformResponseHeader(apiRequestId, execution, apiResponseEntity.getHeaders()))
          .response(response)
          .nextPage(
              (responseNextPage != null && responseNextPage.length() > 0 && !responseNextPage.equals(requestNextPage))
                  ? responseNextPage : null
          )
          .build();

    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException("[COLLECT] Fail to deserialize response json to object", e);
    }
  }

  private <T> String buildUri(String uri, String transformedRequestBody) {
    if (transformedRequestBody == null) {
      return uri;
    }

    try {
      return UriComponentsBuilder
          .fromPath(uri)
          .buildAndExpand(objectMapper.readValue(transformedRequestBody, UriComponentsBuilderParam.class))
          .toUriString();

    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException("Fail to build request uri", e);
    }
  }

  private <T> Map<String, String> transformRequestHeader(String apiRequestId, Execution execution,
      ExecutionRequest<T> executionRequest) {

    return Optional.ofNullable(execution)
        .map(Execution::getApi)
        .map(Api::getTransform)
        .map(Transform::getRequest)
        .map(Api.Request::getHeader)
        .map(header -> {
          try {
            return objectMapper.convertValue(Parser.compileString(header)
                    .apply(objectMapper.convertValue(executionRequest.getHeaders(), JsonNode.class)),
                new TypeReference<Map<String, String>>() {
                });
          } catch (JsltException e) {
            throw new CollectRuntimeException(
                "[COLLECT] JSLT parse or transform fail (request) apiRequestId: " + apiRequestId, e);
          }
        })
        .orElse(executionRequest.getHeaders());
  }

  private <T> String transformRequestBody(String apiRequestId, Execution execution,
      ExecutionRequest<T> executionRequest) {

    try {
      return Optional.ofNullable(execution)
          .map(Execution::getApi)
          .map(Api::getTransform)
          .map(Transform::getRequest)
          .map(Api.Request::getBody)
          .map(body -> {
            try {
              return objectMapper.writeValueAsString(Parser.compileString(body)
                  .apply(objectMapper.readTree(objectMapper.writeValueAsString(executionRequest.getRequest()))));
            } catch (JsltException | JsonProcessingException e) {
              throw new CollectRuntimeException(
                  "[COLLECT] JSLT parse or transform fail (request) apiRequestId: " + apiRequestId, e);
            }
          })
          .orElse(objectMapper.writeValueAsString(executionRequest.getRequest()));
    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException(
          "[COLLECT] JSLT parse or transform fail (request) apiRequestId: " + apiRequestId, e);
    }
  }

  private <T> Map<String, String> transformResponseHeader(String apiRequestId, Execution execution,
      Map<String, String> responseHeader) {

    return Optional.ofNullable(execution)
        .map(Execution::getApi)
        .map(Api::getTransform)
        .map(Transform::getResponse)
        .map(Api.Response::getHeader)
        .map(header -> {
          try {
            return objectMapper.convertValue(
                Parser.compileString(header).apply(objectMapper.convertValue(responseHeader, JsonNode.class))
                , new TypeReference<Map<String, String>>() {
                });
          } catch (JsltException e) {
            throw new CollectRuntimeException(
                "[COLLECT] JSLT parse or transform fail (response) apiRequestId: " + apiRequestId, e);
          }
        })
        .orElse(responseHeader);
  }

  private <T> String transformResponseBody(String apiRequestId, Execution execution, String organizationResopnseBody) {
    return Optional.ofNullable(execution)
        .map(Execution::getApi)
        .map(Api::getTransform)
        .map(Transform::getResponse)
        .map(Api.Response::getBody)
        .map(body -> {
          try {
            return objectMapper.writeValueAsString(Parser.compileString(body)
                .apply(objectMapper.readTree(organizationResopnseBody)));

          } catch (JsltException | JsonProcessingException e) {
            throw new CollectRuntimeException(
                "[COLLECT] JSLT parse or transform fail (response) apiRequestId: " + apiRequestId, e);
          }
        })
        .orElse(organizationResopnseBody);
  }

  private <T> String getRequestNextPage(Api api, ExecutionRequest<T> executionRequest) {

    JsonNode requestJsonNode = objectMapper.convertValue(executionRequest.getRequest(), JsonNode.class);
    Pagination pagination = api.getPagination();

    return Optional.ofNullable(pagination)
        .map(Pagination::getNextPage)
        .map(requestJsonNode::get)
        .map(JsonNode::asText)
        .orElse(null);
  }

  private String getResponseNextPage(Api api, String standardResopnseBody) {
    try {
      JsonNode responseJsonNode = objectMapper.readValue(standardResopnseBody, JsonNode.class);

      Pagination pagination = api.getPagination();

      return Optional.ofNullable(pagination)
          .map(Pagination::getNextPage)
          .map(responseJsonNode::get)
          .map(JsonNode::asText)
          .orElse(null);

    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException("Fail to get nextPage from response", e);
    }

  }

  private void logRequest(
      ExecutionContext context,
      String requestId,
      Api api,
      ExecutionRequest executionRequest,
      Map<String, String> transformedRequestHeader,
      String transformedRequestBody
  ) {
    try {
      apiLogger.onRequest(
          context,
          ApiLog.builder()
              .id(requestId)
              .api(api)
              .request(Request.builder()
                  .header(objectMapper.writeValueAsString(executionRequest.getHeaders()))
                  .body(objectMapper.writeValueAsString(executionRequest.getRequest()))
                  .transformedHeader(objectMapper.writeValueAsString(transformedRequestHeader))
                  .transformedBody(transformedRequestBody)
                  .build())
              .build());
    } catch (Throwable t) {
      log.error("Fail to write request log", t);
    }
  }

  private void logResponse(
      ExecutionContext context,
      String requestId,
      Api api,
      ApiResponseEntity responseEntity,
      Map<String, String> standardResponsetHeader,
      String standardResopnseBody
  ) {
    try {
      apiLogger.onResponse(
          context,
          ApiLog.builder()
              .id(requestId)
              .api(api)
              .response(
                  Response.builder()
                      .responseCode(String.valueOf(responseEntity.getHttpStatusCode()))
                      .header(objectMapper.writeValueAsString(responseEntity.getHeaders()))
                      .body(responseEntity.getBody())
                      .transformedHeader(objectMapper.writeValueAsString(standardResponsetHeader))
                      .transformedBody(standardResopnseBody)
                      .build()
              ).build());
    } catch (Throwable t) {
      log.error("Fail to write response log", t);
    }
  }
}
