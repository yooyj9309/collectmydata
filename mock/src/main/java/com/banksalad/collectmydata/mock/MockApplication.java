package com.banksalad.collectmydata.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.cloud.contract.wiremock.file.ResourcesFileSource;
import org.springframework.context.annotation.Bean;

import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import java.io.IOException;

@SpringBootApplication
@AutoConfigureWireMock
public class MockApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockApplication.class, args);
	}

	@Bean
	public Options wireMockOptions() {

		final WireMockConfiguration options = WireMockSpring.options();
		options.usingFilesUnderClasspath("mock/src/main/resources");
		options.port(9090);

		return options;
	}
}
