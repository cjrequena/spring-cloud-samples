package com.cjrequena.sample.domain.model;

import java.io.Serializable;
import java.util.UUID;

public record FooVO(UUID id, String name) implements Serializable {

  public FooVO {
    // Auto-generate ID if null
    if (id == null) {
      id = UUID.randomUUID();
    }
    if (name.isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }
  }
}
