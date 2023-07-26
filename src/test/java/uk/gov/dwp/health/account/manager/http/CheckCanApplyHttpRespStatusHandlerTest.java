package uk.gov.dwp.health.account.manager.http;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import uk.gov.dwp.health.account.manager.exception.CanApplyCheckFailedException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckCanApplyHttpRespStatusHandlerTest {

  final CheckCanApplyHttpRespStatusHandler handler = new CheckCanApplyHttpRespStatusHandler();

  @Test
  void testHasErrorClientSide() throws IOException {
    final ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatus.PRECONDITION_FAILED);
    assertTrue(handler.hasError(response));
  }

  @Test
  void testHasErrorServerSide() throws IOException {
    final ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
    assertTrue(handler.hasError(response));
  }

  @Test
  void testHasNoError() throws IOException {
    final ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatus.OK);
    assertFalse(handler.hasError(response));
  }

  @Test
  void testHandleError() throws IOException {
    final ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatus.OK);
    try {
      handler.handleError(response);
      fail("Expected CanApplyCheckFailedException");
    } catch (final CanApplyCheckFailedException e) {
      assertEquals(HttpStatus.OK.value(), e.getStatusCode());
    }
  }
}
