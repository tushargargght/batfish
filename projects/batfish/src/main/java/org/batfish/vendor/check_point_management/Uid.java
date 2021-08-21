package org.batfish.vendor.check_point_management;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Nonnull;

public final class Uid implements NatInstallTarget {

  @JsonCreator
  public static @Nonnull Uid of(String value) {
    return new Uid(value);
  }

  private Uid(String value) {
    _value = value;
  }

  @JsonValue
  public @Nonnull String getValue() {
    return _value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (!(o instanceof Uid)) {
      return false;
    }
    Uid uid = (Uid) o;
    return _value.equals(uid._value);
  }

  @Override
  public int hashCode() {
    return _value.hashCode();
  }

  private @Nonnull String _value;
}
