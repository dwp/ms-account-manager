package uk.gov.dwp.health.account.manager.constant;

import java.util.HashMap;
import java.util.Map;

public enum STAGE {
  PENDING(0),
  FIRST(1),
  SECONDPLUS(2),
  LOCKED(-1);
  private static final Map<Integer, STAGE> STAGE_MAP = new HashMap<>();

  static {
    for (STAGE s : STAGE.values()) {
      STAGE_MAP.put(s.stage, s);
    }
  }

  private final int stage;

  STAGE(final int stage) {
    this.stage = stage;
  }

  public static STAGE fromInt(int i) {
    STAGE type = STAGE_MAP.get(i);
    if (type == null) {
      throw new IllegalStateException("Unknown stage");
    }
    return type;
  }

  public int current() {
    return stage;
  }
}
