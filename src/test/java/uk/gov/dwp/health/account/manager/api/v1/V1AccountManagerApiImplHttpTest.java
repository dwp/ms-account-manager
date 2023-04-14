package uk.gov.dwp.health.account.manager.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.api.AppControllerAdvise;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.AccountNotFoundException;
import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.IdRequest;
import uk.gov.dwp.health.account.manager.openapi.model.IdentificationResponse;
import uk.gov.dwp.health.account.manager.openapi.model.NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.NinoDetailsRequest;
import uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest;
import uk.gov.dwp.health.account.manager.openapi.model.Totp;
import uk.gov.dwp.health.account.manager.openapi.model.Totp.SourceEnum;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateEmailRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest;
import uk.gov.dwp.health.account.manager.service.V1AccountManagerServices;
import uk.gov.dwp.health.account.manager.service.impl.Account1FAuthImpl;
import uk.gov.dwp.health.account.manager.service.impl.Account2FAuthImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountIdentificationImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateEmailImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdatePasswordImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(
    classes = {
      V1AccountManagerApiImpl.class,
      AppControllerAdvise.class,
      V1AccountManagerApiImpl.class
    })
@WebMvcTest
class V1AccountManagerApiImplHttpTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final PasswordSetResetRequest PASSWORD_REQUEST = new PasswordSetResetRequest();
  private static final NewAccountRequest NEW_ACCOUNT_REQUEST = new NewAccountRequest();
  private static final IdRequest ID_REQUEST = new IdRequest();
  @Autowired private MockMvc mockMvc;
  @MockBean private V1AccountManagerServices v1AccountManagerServices;

  @BeforeAll
  static void setup() {
    MAPPER.registerModule(new ParameterNamesModule());
    MAPPER.registerModule(new JavaTimeModule());
    MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    PASSWORD_REQUEST.setRef("b0a0d4fb-e6c8-419e-8cb9-af45914bd1a6");

    NEW_ACCOUNT_REQUEST.setNino("RN000001C");
    NEW_ACCOUNT_REQUEST.setEmail("test@dwp.gov.uk");
    NEW_ACCOUNT_REQUEST.setMobilePhone("07777777777");
    NEW_ACCOUNT_REQUEST.setSurname("Surname");
    NEW_ACCOUNT_REQUEST.setForename("Forename");
    NEW_ACCOUNT_REQUEST.setPostcode("LS1 7XX");
    NEW_ACCOUNT_REQUEST.setDob(LocalDate.parse("2020-05-15"));
    NEW_ACCOUNT_REQUEST.setLanguage(NewAccountRequest.LanguageEnum.EN);

    ID_REQUEST.setDob(LocalDate.parse("2020-05-15"));
    ID_REQUEST.setNino("RN000001C");
    ID_REQUEST.setEmail("test@dwp.gov.uk");
  }

  private static Stream<Arguments> invalidNewAccountRequests() throws JsonProcessingException {
    String invalidNullRequest = new ObjectMapper().writeValueAsString(new NewAccountRequest());
    String invalidEmailRequest =
        newAccountRequestFixture(
            "testemail.com",
            "07777777777",
            NewAccountRequest.LanguageEnum.EN,
            "2020-05-15",
            "name surname",
            "RN000079B");
    String invalidMobileRequest =
        newAccountRequestFixture(
            "test@dwp.gov.uk",
            "text",
            NewAccountRequest.LanguageEnum.EN,
            "2020-05-15",
            "name surname",
            "RN000079B");
    String invalidDateRequest =
        newAccountRequestFixture(
            "test@dwp.gov.uk",
            "07876543245",
            NewAccountRequest.LanguageEnum.CY,
            "15/10/2002",
            "forename surname",
            "RN000065A");
    String invalidDelimiterRequest =
        newAccountRequestFixture(
            "test@dwp.gov.uk",
            "07876543245",
            NewAccountRequest.LanguageEnum.CY,
            "2020/05/15",
            "forename surname",
            "RN000065A");
    String invalidNinoRequestWithSpaces =
        newAccountRequestFixture(
            "test@dwp.gov.uk",
            "07777777777",
            NewAccountRequest.LanguageEnum.EN,
            "2020-05-15",
            "name surname",
            "A A123456C");
    String invalidNinoRequest =
        newAccountRequestFixture(
            "test@dwp.gov.uk",
            "07777777777",
            NewAccountRequest.LanguageEnum.EN,
            "2020-05-15",
            "name surname",
            "1A123456C");

    return Stream.of(
        Arguments.of(invalidNullRequest),
        Arguments.of(invalidEmailRequest),
        Arguments.of(invalidMobileRequest),
        Arguments.of(invalidDateRequest),
        Arguments.of(invalidDelimiterRequest),
        Arguments.of(invalidNinoRequestWithSpaces),
        Arguments.of(invalidNinoRequest));
  }

  private static String newAccountRequestFixture(
      String email,
      String mobilePhone,
      NewAccountRequest.LanguageEnum language,
      String dob,
      String name,
      String nino) {

    return String.format(
        "{\n"
            + "    \"email\": \"%s\",\n"
            + "    \"nino\": \"%s\",\n"
            + "    \"dob\": \"%s\",\n"
            + "    \"mobile_phone\": \"%s\",\n"
            + "    \"name\": \"%s\",\n"
            + "    \"language\": \"%s\"\n"
            + "}",
        email, nino, dob, mobilePhone, name, language.toString());
  }

  private static Stream<Arguments> invalidIdRequests() throws JsonProcessingException {
    String invalidNullRequest = new ObjectMapper().writeValueAsString(new IdRequest());
    String invalidEmailRequest = newIdRequestFixture("testemail.com", "2020-05-15", "RN000079B");
    String invalidNinoRequest = newIdRequestFixture("test@dwp.gov.uk", "2020-05-15", "test");
    String invalidDateRequest = newIdRequestFixture("test@dwp.gov.uk", "15-05-2002", "RN000079B");

    return Stream.of(
        Arguments.of(invalidNullRequest),
        Arguments.of(invalidEmailRequest),
        Arguments.of(invalidNinoRequest),
        Arguments.of(invalidDateRequest));
  }

  private static String newIdRequestFixture(String email, String dob, String nino) {
    return String.format(
        "{\n"
            + "    \"email\": \"%s\",\n"
            + "    \"nino\": \"%s\",\n"
            + "    \"dob\": \"%s\",\n"
            + "    \"generate_email_totp\": false,\n"
            + "    \"generate_sms_totp\": false\n"
            + "}",
        email, nino, dob);
  }

  @ParameterizedTest
  @MethodSource(value = "invalidNewAccountRequests")
  void testCreateAccountFailRequestValidation(final String accountRequest) throws Exception {
    mockMvc
        .perform(
            post("/v1/account/create")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(accountRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testSuccessCreateAccount() throws Exception {
    var accountReturn = new AccountReturn();
    accountReturn.setRef("b0a0d4fb-e6c8-419e-8cb9-af45914bd1a6");
    var response = ResponseEntity.status(HttpStatus.CREATED).body(accountReturn);
    var createAccount = mock(AccountCreateImpl.class);
    when(v1AccountManagerServices.getAccountCreate()).thenReturn(createAccount);
    when(createAccount.doCreateAccount(any(NewAccountRequest.class))).thenReturn(response);
    mockMvc
        .perform(
            post("/v1/account/create")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(NEW_ACCOUNT_REQUEST)))
        .andExpect(status().isCreated());
    var captor = ArgumentCaptor.forClass(NewAccountRequest.class);
    verify(v1AccountManagerServices).getAccountCreate();
    verify(createAccount).doCreateAccount(captor.capture());
    assertThat(captor.getValue()).isEqualTo(NEW_ACCOUNT_REQUEST);
  }

  @Test
  void testUnauthorisedForCreateAccount() throws Exception {
    var service = mock(AccountCreateImpl.class);
    when(v1AccountManagerServices.getAccountCreate()).thenReturn(service);
    when(service.doCreateAccount(any(NewAccountRequest.class)))
        .thenThrow(new AccountExistException("Account creation fail, account already exist"));
    mockMvc
        .perform(
            post("/v1/account/create")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(NEW_ACCOUNT_REQUEST)))
        .andExpect(status().isConflict());
  }

  @Test
  void testSuccessCreatePassword() throws Exception {
    PASSWORD_REQUEST.setPassword("Password_123");
    var totpList = new ArrayList<Totp>();
    var t = new Totp();
    t.setCode("111222");
    t.setSource(Totp.SourceEnum.MOBILE);
    totpList.add(t);
    PASSWORD_REQUEST.setTotp(totpList);
    ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.CREATED).build();
    var service = mock(AccountUpdatePasswordImpl.class);
    when(v1AccountManagerServices.getUpdatePassword()).thenReturn(service);
    when(service.updatePassword(any(PasswordSetResetRequest.class))).thenReturn(response);
    mockMvc
        .perform(
            post("/v1/account/password")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(PASSWORD_REQUEST)))
        .andExpect(status().isCreated());
    var argument = ArgumentCaptor.forClass(PasswordSetResetRequest.class);
    verify(service).updatePassword(argument.capture());
    assertThat(argument.getValue()).isEqualTo(PASSWORD_REQUEST);
  }

  @Test
  void testSuccessUpdatePassword() throws Exception {
    PASSWORD_REQUEST.setPassword("Password_123");
    var totpList = new ArrayList<Totp>();
    var t = new Totp();
    t.setCode("111222");
    t.setSource(Totp.SourceEnum.MOBILE);
    totpList.add(t);
    PASSWORD_REQUEST.setTotp(totpList);
    ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.OK).build();
    var service = mock(AccountUpdatePasswordImpl.class);
    when(v1AccountManagerServices.getUpdatePassword()).thenReturn(service);
    when(service.updatePassword(any(PasswordSetResetRequest.class))).thenReturn(response);
    mockMvc
        .perform(
            post("/v1/account/password")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(PASSWORD_REQUEST)))
        .andExpect(status().isOk());
    var argument = ArgumentCaptor.forClass(PasswordSetResetRequest.class);
    verify(service).updatePassword(argument.capture());
    assertThat(argument.getValue()).isEqualTo(PASSWORD_REQUEST);
  }

  @Test
  void testUnauthorisedForPassword() throws Exception {
    PASSWORD_REQUEST.setPassword("Password_123");
    var totpList = new ArrayList<Totp>();
    var t = new Totp();
    t.setCode("111222");
    t.setSource(Totp.SourceEnum.MOBILE);
    totpList.add(t);
    PASSWORD_REQUEST.setTotp(totpList);
    ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    var service = mock(AccountUpdatePasswordImpl.class);
    when(v1AccountManagerServices.getUpdatePassword()).thenReturn(service);
    when(service.updatePassword(any(PasswordSetResetRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/v1/account/password")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(PASSWORD_REQUEST)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testCreateUpdatePasswordFailNullRequestValidation() throws Exception {
    mockMvc
        .perform(
            post("/v1/account/password")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(new PasswordSetResetRequest())))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testCreateUpdatePasswordFailInvalidPasswordRequestValidation() throws Exception {
    PASSWORD_REQUEST.setPassword("test");
    mockMvc
        .perform(
            post("/v1/account/password")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(PASSWORD_REQUEST)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testCreateUpdatePasswordFailInvalidTotpRequestValidation() throws Exception {
    PASSWORD_REQUEST.setPassword("Password_123");
    var totpList = new ArrayList<Totp>();
    var t = new Totp();
    t.setCode("test");
    t.setSource(Totp.SourceEnum.MOBILE);
    totpList.add(t);
    PASSWORD_REQUEST.setTotp(totpList);
    mockMvc
        .perform(
            post("/v1/account/password")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(PASSWORD_REQUEST)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testSuccessForIdentify() throws Exception {
    var ref = new IdentificationResponse();
    ResponseEntity<IdentificationResponse> response = ResponseEntity.ok().body(ref);
    var service = mock(AccountIdentificationImpl.class);
    when(v1AccountManagerServices.getAccountIdentification()).thenReturn(service);
    when(service.doIdentification(any(IdRequest.class))).thenReturn(response);
    mockMvc
        .perform(
            post("/v1/account/identification")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(ID_REQUEST)))
        .andExpect(status().isOk());

    var argument = ArgumentCaptor.forClass(IdRequest.class);
    verify(service).doIdentification(argument.capture());
    assertThat(argument.getValue()).isEqualTo(ID_REQUEST);
  }

  @Test
  void testUnauthorisedForIdentify() throws Exception {
    var service = mock(AccountIdentificationImpl.class);
    when(v1AccountManagerServices.getAccountIdentification()).thenReturn(service);
    when(service.doIdentification(any(IdRequest.class)))
        .thenThrow(
            new AccountNotFoundException(String.format("%s account not found", "test@dwp.gov.uk")));
    mockMvc
        .perform(
            post("/v1/account/identification")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(ID_REQUEST)))
        .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @MethodSource(value = "invalidIdRequests")
  void testIdentifyFailRequestValidation(final String idRequest) throws Exception {
    mockMvc
        .perform(
            post("/v1/account/identification")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(idRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testSuccessValidFirstFactor() throws Exception {
    var request = new ValidEmailPasswordRequest();
    request.setEmail("test@dwp.gov.uk");
    request.setPassword("Password_123");
    var ref = new AccountReturn();
    ResponseEntity<AccountReturn> response = ResponseEntity.ok().body(ref);
    var service = mock(Account1FAuthImpl.class);
    when(v1AccountManagerServices.getAccount1FAuth()).thenReturn(service);
    when(service.do1FAuthentication(any(ValidEmailPasswordRequest.class))).thenReturn(response);
    mockMvc
        .perform(
            post("/v1/account/verify/login")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isOk());
    var argument = ArgumentCaptor.forClass(ValidEmailPasswordRequest.class);
    verify(service).do1FAuthentication(argument.capture());
    assertThat(argument.getValue()).isEqualTo(request);
  }

  @Test
  void testUnauthorizedValidFirstFactor() throws Exception {
    var request = new ValidEmailPasswordRequest();
    request.setEmail("test@dwp.gov.uk");
    request.setPassword("Password_123");
    var service = mock(Account1FAuthImpl.class);
    when(v1AccountManagerServices.getAccount1FAuth()).thenReturn(service);
    when(service.do1FAuthentication(any(ValidEmailPasswordRequest.class)))
        .thenThrow(new AccountAuthFailException("Authentication Failed"));
    mockMvc
        .perform(
            post("/v1/account/verify/login")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testFirstFactorFailNullRequestValidation() throws Exception {
    mockMvc
        .perform(
            post("/v1/account/verify/login")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(new ValidEmailPasswordRequest())))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testFirstFactorFailEmailRequestValidation() throws Exception {
    var request = new ValidEmailPasswordRequest();
    request.setEmail("testemail.com");
    request.setPassword("Password_123");
    mockMvc
        .perform(
            post("/v1/account/verify/login")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testValid2factorFailRequestValidationOnEmptyPayload() throws Exception {
    mockMvc
        .perform(
            post("/v1/account/verify/totp")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(new ValidTotpRequest())))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testValid2fFactorFailRequestValidationOnNoneDigitTotp() throws Exception {
    var request = new ValidTotpRequest();
    request.setRef("b0a0d4fb-e6c8-419e-8cb9-af45914bd1a6");
    var totp = new Totp();
    totp.setCode("test");
    totp.setSource(Totp.SourceEnum.EMAIL);
    request.setTotp(totp);
    mockMvc
        .perform(
            post("/v1/account/verify/totp")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testValid2fFactorFailNullRequestValidation() throws Exception {
    mockMvc
        .perform(
            post("/v1/account/verify/totp")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(new ValidTotpRequest())))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testSuccessValid2fFactor() throws Exception {
    var request = new ValidTotpRequest();
    request.setRef("b0a0d4fb-e6c8-419e-8cb9-af45914bd1a6");
    var totp = new Totp();
    totp.setSource(SourceEnum.MOBILE);
    totp.setCode("111222");
    request.setTotp(totp);
    var service = mock(Account2FAuthImpl.class);
    when(v1AccountManagerServices.getAccount2FAuth()).thenReturn(service);
    when(service.do2FAuthentication(any(ValidTotpRequest.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
    mockMvc
        .perform(
            post("/v1/account/verify/totp")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isOk());
    var argument = ArgumentCaptor.forClass(ValidTotpRequest.class);
    verify(service).do2FAuthentication(argument.capture());
    assertThat(argument.getValue()).isEqualTo(request);
  }

  @Test
  void testAccountDetailsFailRequestNotFoundEndpoint() throws Exception {
    String request = "";
    mockMvc
        .perform(
            get("/v1/account/details/" + request)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isMethodNotAllowed());
  }

  @Test
  @DisplayName("test post query request of account details by nino")
  void testPostQueryRequestOfAccountDetailsByNino() throws Exception {
    var request = new NinoDetailsRequest();
    request.setNino("RN000008B");
    var service = mock(AccountGetClaimantDetailsImpl.class);
    when(v1AccountManagerServices.getClaimantDetails()).thenReturn(service);
    var result = List.of(new AccountDetails());
    when(service.getAccountDetailsByNino(anyString())).thenReturn(ResponseEntity.ok().body(result));
    mockMvc
        .perform(
            post("/v1/account/details")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isOk());
    var argument = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByNino(argument.capture());
    assertThat(argument.getValue()).isEqualTo("RN000008B");
  }

  @Test
  void testUnauthorisedForAccountDetails() throws Exception {
    var request = "5ef5ea24d99bf02e6dcda009";
    var service = mock(AccountGetClaimantDetailsImpl.class);
    when(v1AccountManagerServices.getClaimantDetails()).thenReturn(service);
    when(service.getAccountDetailsByRef(anyString()))
        .thenThrow(
            new AccountNotFoundException(String.format("%s account not found", "test@dwp.gov.uk")));
    mockMvc
        .perform(
            get("/v1/account/details/" + request)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testSuccessAccountDetails() throws Exception {
    var request = "5ef5ea24d99bf02e6dcda009";
    var service = mock(AccountGetClaimantDetailsImpl.class);
    when(v1AccountManagerServices.getClaimantDetails()).thenReturn(service);
    when(service.getAccountDetailsByRef(anyString()))
        .thenReturn(ResponseEntity.ok().body(new AccountDetails()));
    mockMvc
        .perform(
            get("/v1/account/details/" + request)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isOk());
    var argument = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByRef(argument.capture());
    assertThat(argument.getValue()).isEqualTo(request);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Testperson Smithz",
        "Taylor-King",
        "DWP.Health",
        "Knight's Kingrarename",
        "Surname is lastname"
      })
  @DisplayName("Test all valid surname successfully")
  void testSuccessSurname(final String surname) throws Exception {
    var service = mock(AccountCreateImpl.class);
    when(v1AccountManagerServices.getAccountCreate()).thenReturn(service);
    var request = new NewAccountRequest();
    request.setPostcode("LS1 7XX");
    request.setForename(TestFixtures.FORENAME);
    request.setSurname(surname);
    request.setEmail(TestFixtures.EMAIL);
    request.setDob(TestFixtures.DOB);
    request.setLanguage(NewAccountRequest.LanguageEnum.EN);
    request.setMobilePhone(TestFixtures.MOBILE);
    request.setNino(TestFixtures.NINO);
    mockMvc
        .perform(
            post("/v1/account/create")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isOk());
    verify(service).doCreateAccount(request);
    assertThat(request.getSurname()).isEqualTo(surname);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Testperson Smithz",
        "Dave-King",
        "DWP.Health",
        "Knight's Kingrarename",
        "Forename is firstname"
      })
  @DisplayName("Test all valid forename successfully")
  void testSuccessForename(final String forename) throws Exception {
    var request = new NewAccountRequest();
    var service = mock(AccountCreateImpl.class);
    when(v1AccountManagerServices.getAccountCreate()).thenReturn(service);
    request.setPostcode("LS1 7XX");
    request.setSurname(TestFixtures.SURNAME);
    request.setForename(forename);
    request.setEmail(TestFixtures.EMAIL);
    request.setDob(TestFixtures.DOB);
    request.setLanguage(NewAccountRequest.LanguageEnum.EN);
    request.setMobilePhone(TestFixtures.MOBILE);
    request.setNino(TestFixtures.NINO);
    mockMvc
        .perform(
            post("/v1/account/create")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isOk());
    verify(service).doCreateAccount(request);
    assertThat(request.getForename()).isEqualTo(forename);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(
      strings = {
        "invalid name",
        "Consecutive   punctuation",
        "Spaces ; before  after",
        "Last punc."
      })
  @DisplayName("Test surname mandatory and regex validation")
  void testSurnameMandatoryAndValidateRegex(final String surname) throws Exception {
    var request = new NewAccountRequest();
    request.setPostcode("LS1 7XX");
    request.setForename(TestFixtures.FORENAME);
    request.setSurname(surname);
    request.setEmail(TestFixtures.EMAIL);
    request.setDob(TestFixtures.DOB);
    request.setLanguage(NewAccountRequest.LanguageEnum.EN);
    request.setMobilePhone(TestFixtures.MOBILE);
    request.setNino(TestFixtures.NINO);
    mockMvc
        .perform(
            post("/v1/account/create")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(
      strings = {
        "invalid name",
        "Consecutive   punctuation",
        "Spaces ; before  after",
        "Last punc."
      })
  @DisplayName("Test forename mandatory and regex validation")
  void testForenameMandatoryAndValidateRegex(final String forename) throws Exception {
    var request = new NewAccountRequest();
    request.setPostcode("LS1 7XX");
    request.setSurname(TestFixtures.SURNAME);
    request.setForename(forename);
    request.setEmail(TestFixtures.EMAIL);
    request.setDob(TestFixtures.DOB);
    request.setLanguage(NewAccountRequest.LanguageEnum.EN);
    request.setMobilePhone(TestFixtures.MOBILE);
    request.setNino(TestFixtures.NINO);
    mockMvc
        .perform(
            post("/v1/account/create")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Test postcode mandatory")
  void testPostcodeMandatory(final String postcode) throws Exception {
    var request = new NewAccountRequest();
    request.setPostcode(postcode);
    request.setForename(TestFixtures.FORENAME);
    request.setSurname(TestFixtures.SURNAME);
    request.setEmail(TestFixtures.EMAIL);
    request.setDob(TestFixtures.DOB);
    request.setLanguage(NewAccountRequest.LanguageEnum.EN);
    request.setMobilePhone(TestFixtures.MOBILE);
    request.setNino(TestFixtures.NINO);
    mockMvc
        .perform(
            post("/v1/account/create")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("test update email new email mandatory")
  void testUpdateEmailNewEmailMandatory() throws Exception {
    var request = new UpdateEmailRequest();
    request.setNewEmail("");
    request.setCurrentEmail("current@dwp.gov.uk");
    mockMvc
        .perform(
            patch("/v1/account/email")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("test update email current email mandatory")
  void testUpdateEmailCurrentEmailMandatory() throws Exception {
    var request = new UpdateEmailRequest();
    request.setNewEmail("new@dwp.gov.uk");
    request.setCurrentEmail("");
    mockMvc
        .perform(
            patch("/v1/account/email")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("test update email accepted and account details returned by account service")
  void testUpdateEmailAcceptedAndAccountDetailsReturnedByAccountService() throws Exception {
    var request = new UpdateEmailRequest();
    request.setNewEmail("new@dwp.gov.uk");
    request.setCurrentEmail("current@dwp.gov.uk");
    var service = mock(AccountUpdateEmailImpl.class);
    when(v1AccountManagerServices.getUpdateEmail()).thenReturn(service);
    when(service.updateEmail(any()))
        .thenReturn(ResponseEntity.accepted().body(new AccountDetails()));
    var argumentCaptor = ArgumentCaptor.forClass(UpdateEmailRequest.class);
    mockMvc
        .perform(
            patch("/v1/account/email")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isAccepted());
    verify(service).updateEmail(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).isEqualTo(request);
  }
}
