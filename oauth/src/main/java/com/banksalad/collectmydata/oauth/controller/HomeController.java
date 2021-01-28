package com.banksalad.collectmydata.oauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ping")
public class HomeController {

  @GetMapping("/health")
  public Mono<String> healthCheck() {
    return Mono.just("pong");
  }
}
