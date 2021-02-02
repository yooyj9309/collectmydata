package com.banksalad.collectmydata.common.collect.api;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Getter
public class Api {

  private Api(String id, String name, String endpoint, String method, Transform transform, Pagination pagniation) {
    this.id = id;
    this.name = name;
    this.endpoint = endpoint;
    this.method = method;
    this.transform = transform;
    this.pagination = pagniation;
  }

  public static ApiBuilder builder() {
    return new ApiBuilder();
  }

  final private String id;
  final private String name;
  final private String endpoint;
  final private String method;
  final private Transform transform;
  final private Pagination pagination;

  @Getter
  public static class Transform {

    public Transform(Request request, Response response) {
      this.request = request;
      this.response = response;
    }

    private Request request;
    private Response response;
  }

  @Getter
  public static class Request {

    private Request(String header, String body) {
      this.header = readFile(header);
      this.body = readFile(body);
    }

    private String header;
    private String body;
  }

  @Getter
  public static class Response {

    private Response(String header, String body) {
      this.header = readFile(header);
      this.body = readFile(body);
    }

    private String header;
    private String body;
  }

  public static Request request(String header, String body) {
    return new Request(header, body);
  }

  public static Response response(String header, String body) {
    return new Response(header, body);
  }

  private static String readFile(String fileInClassPath) {
    InputStream inputStream = Api.class.getClassLoader().getResourceAsStream(fileInClassPath);

    if (inputStream == null) {
      throw new CollectRuntimeException("Fail to read JSLT, file is not exit: " + fileInClassPath);
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String s = reader.lines().collect(Collectors.joining("\n"));
      return escapeUnicode(s);

    } catch (IOException e) {
      throw new CollectRuntimeException("Fail to read JSLT", e);
    }
  }

  public static class ApiBuilder {

    private String id;
    private String name;
    private String endpoint;
    private String method;
    private Transform transform;
    private Pagination pagination;

    public ApiBuilder id(String id) {
      this.id = id;
      return this;
    }

    public ApiBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ApiBuilder endpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    public ApiBuilder method(String method) {
      this.method = method;
      return this;
    }

    public ApiBuilder transform(Request request, Response response) {
      this.transform = new Transform(request, response);
      return this;
    }


    public ApiBuilder pagination(Pagination pagination) {
      this.pagination = pagination;
      return this;
    }


    public Api build() {
      return new Api(id, name, endpoint, method, transform, pagination);
    }
  }

  private static final char[] hexChar = {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
  };

  private static String escapeUnicode(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if ((c >> 7) > 0) {
        sb.append("\\u");
        sb.append(hexChar[(c >> 12) & 0xF]); // append the hex character for the left-most 4-bits
        sb.append(hexChar[(c >> 8) & 0xF]);  // hex for the second group of 4-bits from the left
        sb.append(hexChar[(c >> 4) & 0xF]);  // hex for the third group
        sb.append(hexChar[c & 0xF]);         // hex for the last group, e.g., the right most 4-bits
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
