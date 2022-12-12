package com.cjrequena.sample.dto;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.serializer.LocalDateDeserializer;
import com.cjrequena.sample.dto.serializer.LocalDateSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder(value = {
  "id",
  "owner",
  "balance",
  "creation_date",
  "version"
})
@Schema
public class AccountDTO implements Serializable {

  @JsonProperty(value = "id")
  @Schema(accessMode = READ_ONLY)
  private UUID id;

  @NotNull(message = "owner is a required field")
  @JsonProperty(value = "owner", required = true)
  private String owner;

  @NotNull(message = "balance is a required field")
  @JsonProperty(value = "balance", required = true)
  private BigDecimal balance;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "creation_date")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
  @Schema(accessMode = READ_ONLY)
  private LocalDate creationDate;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "version")
  @Schema(accessMode = READ_ONLY)
  private Long version;

}
