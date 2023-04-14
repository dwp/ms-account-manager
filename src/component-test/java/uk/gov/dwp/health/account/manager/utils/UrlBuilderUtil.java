package uk.gov.dwp.health.account.manager.utils;

import static io.restassured.RestAssured.baseURI;

public class UrlBuilderUtil {

  public static String postAccountUrl() {
    return baseURI + "/v4/account/create/";
  }

  public static String patchClaimantDetailsUrl() {
    return baseURI + "/v4/account/claimantdetails/";
  }

  public static String getAccountByIDUrl(String accountRef) {
    return baseURI + "/v4/account/details/id/" + accountRef + "";
  }

  public static String getAccountByEmailUrl(String email) {
    return baseURI + "/v4/account/details/email/" + email + "";
  }

  public static String getAccountByNinoUrl(String nino) {
    return baseURI + "/v4/account/details/nino/" + nino + "";
  }

  public static String getMobileNumbersByClaimantIdUrl(String claimantIds) {
    return baseURI + "/v3/account/mobilephone/claimantid/" + claimantIds;
  }

  public static String postIdentifyUrl() {
    return baseURI + "/v3/account/identification/";
  }

  public static String postTotpUrl() {
    return baseURI + "/v3/account/verify/totp";
  }

  public static String postPasswordUrl() {
    return baseURI + "/v3/account/password/";
  }

  public static String postLoginUrl() {
    return baseURI + "/v3/account/verify/login/";
  }
}
