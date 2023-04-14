package uk.gov.dwp.health.account.manager.http.totp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TotpVerifyRequestTest {

  private TotpVerifyRequest underTest;

  @Test
  void testCreateJsonStringFromObject() {
    underTest = new TotpVerifyRequest();
    underTest.setSecret("THIS_IS_SECRET");
    underTest.setTotp("123456");
    assertThat(underTest.toJson()).isEqualTo("{\"secret\":\"THIS_IS_SECRET\",\"totp\":\"123456\"}");
  }

  @Test
  void testWhenExceptionThrowErrorLoggedEmptyJsonReturned() throws Exception {
    underTest = new TotpVerifyRequest();
    ObjectMapper mapper = mock(ObjectMapper.class);
    ReflectionTestUtils.setField(underTest, "mapper", mapper);
    when(mapper.writeValueAsString(any(TotpVerifyRequest.class)))
        .thenThrow(new JsonProcessingException("") {});
    String actual = underTest.toJson();
    assertThat(actual).isEqualTo("{}");
  }
}
