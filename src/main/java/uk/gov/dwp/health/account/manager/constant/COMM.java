package uk.gov.dwp.health.account.manager.constant;

public enum COMM {
  EMAIL("EMAIL"),
  MOBILE("MOBILE");

  private final String method;

  COMM(final String method) {
    this.method = method;
  }

  public String method() {
    return method;
  }
}
