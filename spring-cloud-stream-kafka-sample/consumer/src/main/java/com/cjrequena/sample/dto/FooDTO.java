package com.cjrequena.sample.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class FooDTO implements Serializable {
  private UUID id;
  private String name;
}
