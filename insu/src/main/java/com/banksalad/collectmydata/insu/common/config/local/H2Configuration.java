package com.banksalad.collectmydata.insu.common.config.local;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import org.h2.tools.Server;

import java.sql.SQLException;

/**
 * webflux 환경에서 h2를 사용하기 위한 configuration 입니다. 개발 편의상 설정하였고 staging DB와 연결 후 제거 예정입니다.
 * <p>
 * h2 connection url : localhost:8081
 */
@Profile("local")
@Configuration
public class H2Configuration {

  private Server webServer;

  @EventListener(ContextRefreshedEvent.class)
  public void start() throws SQLException {
    this.webServer = Server.createWebServer("-webPort", "8081", "-tcpAllowOthers").start();
  }

  @EventListener(ContextClosedEvent.class)
  public void stop() {
    this.webServer.stop();
  }
}
