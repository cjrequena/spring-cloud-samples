package com.cjrequena.sample.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public enum EStatus {

  PENDING("PENDING"),
  COMPLETED("COMPLETED"),
  REJECTED("REJECTED"),
  FAILED("FAILED");

  @JsonValue
  @Getter
  private final String value;

  EStatus(String value) {
    this.value = value;
  }

  @JsonCreator
  public static EStatus parse(String value) {
    return Arrays.stream(EStatus.values()).filter(e -> e.getValue().equals(value)).findFirst().orElse(null);
  }

}
