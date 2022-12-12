package com.cjrequena.sample.dto;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.common.EStatus;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder(value = {
  "id",
  "account_id",
  "total",
  "creation_date",
  "status",
  "description",
  "version"
})
@Schema
public class OrderDTO {

  @JsonProperty(value = "id")
  @Schema(accessMode = READ_ONLY)
  private Integer id;

  @NotNull(message = "account_id is a required field")
  @JsonProperty(value = "account_id", required = true)
  private UUID accountId;

  @NotNull(message = "total is a required field")
  @JsonProperty(value = "total", required = true)
  private BigDecimal total;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "creation_date")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
  @Schema(accessMode = READ_ONLY)
  private LocalDate creationDate;

  @JsonProperty(value = "status")
  @Schema(accessMode = READ_ONLY)
  private EStatus status = EStatus.PENDING; // Default value

  @JsonProperty(value = "description")
  @Schema(accessMode = READ_ONLY)
  private String description;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "version")

  @Schema(accessMode = READ_ONLY)
  private Long version;

}
