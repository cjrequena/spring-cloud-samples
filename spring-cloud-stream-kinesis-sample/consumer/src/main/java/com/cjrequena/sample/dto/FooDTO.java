package com.cjrequena.sample.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder(value = {
  "id",
  "name"
})
@Schema
public class FooDTO implements Serializable {

  @JsonProperty(value = "id")
  @Schema(accessMode = READ_ONLY)
  private UUID id;

  @NotNull(message = "name is a required field")
  @JsonProperty(value = "name", required = true)
  private String name;
}
