package com.banksalad.collectmydata.common.collect.execution;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

import lombok.Getter;

import java.util.function.BiConsumer;

@Getter
public class Execution {

  private final Api api;
  private final Class as;
  private BiConsumer<ExecutionContext, Throwable> exceptionally;

  private Execution(
      Api api,
      Class as,
      Pagination pagination
  ) {
    this.api = api;
    this.as = as;
    // TODO : pagination은 Api로 가야 하지 않나?
  }

  public static ExecutionBuilder create() {
    return new ExecutionBuilder();
  }

  public static class ExecutionBuilder {

    private Api api;
    private Class as;
    private Pagination pagination;

    public ExecutionBuilder exchange(Api api) {
      this.api = api;
      return this;
    }

    public ExecutionBuilder as(Class as) {
      this.as = as;
      return this;
    }

    public ExecutionBuilder paging(Pagination pagination) {
      this.pagination = pagination;
      return this;
    }

    public Execution build() {

      return new Execution(
          this.api,
          this.as,
          this.pagination);
    }
  }
}
