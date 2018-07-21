package com.mgb.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyConfig
{
  @Bean
  RestTemplate restTemplate()
  {
    return new RestTemplate();
  }
}