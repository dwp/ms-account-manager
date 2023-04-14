package uk.gov.dwp.health.account.manager.http.totp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TotpGenerateRequestTest {

  private TotpGenerateRequest underTest;

  @Test
  void testCreateJsonStringFromObject() {
    underTest = new TotpGenerateRequest();
    underTest.setSecret("THIS_IS_SECRET");
    underTest.setComm("MOBILE");
    underTest.setContact("0777777778");
    assertThat(underTest.toJson())
        .isEqualTo(
            "{\"comm\":\"MOBILE\",\"contact\":\"0777777778\",\"secret\":\"THIS_IS_SECRET\"}");
  }

  @Test
  void testWhenExceptionThrowErrorLoggedEmptyJsonReturned() throws Exception {
    underTest = new TotpGenerateRequest();
    ObjectMapper mapper = mock(ObjectMapper.class);
    ReflectionTestUtils.setField(underTest, "mapper", mapper);
    when(mapper.writeValueAsString(any(TotpGenerateRequest.class)))
        .thenThrow(new JsonProcessingException("") {});
    String actual = underTest.toJson();
    assertThat(actual).isEqualTo("{}");
  }
}
