package com.cjrequena.sample.domain.model.vo;

import java.io.Serializable;
import java.util.UUID;

public record FooVO(UUID id, String name) implements Serializable {

  public FooVO {
    if (id == null) {
      throw new IllegalArgumentException("Id cannot be null");
    }
    if (name.isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }
  }
}
