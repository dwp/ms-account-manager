package uk.gov.dwp.health.account.manager.http.totp;

import uk.gov.dwp.health.account.manager.entity.Region;

public interface Request {

  String toJson();

  Region getRegion();

  void setRegion(Region region);
}
