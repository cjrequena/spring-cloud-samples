package com.cjrequena.sample.dto;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.common.EStatus;
import com.cjrequena.sample.dto.serializer.OffsetDateTimeDeserializer;
import com.cjrequena.sample.dto.serializer.OffsetDateTimeSerializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder(value = {
  "id",
  "account_id",
  "description",
  "total",
  "status",
  "created_at",
  "updated_at",
  "version"
})
@Schema
public class OrderDTO {

  @JsonProperty(value = "id")
  @Schema(accessMode = READ_ONLY)
  private UUID id;

  @NotNull(message = "account_id is a required field")
  @JsonProperty(value = "account_id", required = true)
  private UUID accountId;

  @JsonProperty(value = "description")
  @Schema(accessMode = READ_ONLY)
  private String description;

  @NotNull(message = "total is a required field")
  @JsonProperty(value = "total", required = true)
  private BigDecimal total;

  @JsonProperty(value = "status")
  @Schema(accessMode = READ_ONLY)
  private EStatus status = EStatus.PENDING; // Default value

  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.ISO_OFFSET_DATE_TIME)
  @Schema(accessMode = READ_ONLY)
  private OffsetDateTime createdAt;

  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.ISO_OFFSET_DATE_TIME)
  @Schema(accessMode = READ_ONLY)
  private OffsetDateTime updatedAt;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "version")
  @Schema(accessMode = READ_ONLY)
  private Long version;

}
