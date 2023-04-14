package uk.gov.dwp.health.account.manager.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public interface RestClientService<T> {

  default ResponseEntity<Void> postRequest(
      final RestTemplate template, final String url, final String jsonPayload) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
    return template.exchange(url, HttpMethod.POST, requestEntity, Void.class);
  }

  boolean postGenerateRequest(T request);

  boolean postVerifyRequest(T request);
}
