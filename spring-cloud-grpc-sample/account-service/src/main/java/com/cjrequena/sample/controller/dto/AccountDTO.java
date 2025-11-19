package com.cjrequena.sample.controller.dto;

import com.cjrequena.sample.shared.common.Constant;
import com.cjrequena.sample.shared.common.util.serializer.OffsetDateTimeDeserializer;
import com.cjrequena.sample.shared.common.util.serializer.OffsetDateTimeSerializer;
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
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
  "owner",
  "balance",
  "created_at",
  "updated_at",
  "version"
})
@Schema
public class AccountDTO implements Serializable {

  @Schema(accessMode = READ_ONLY)
  private UUID id;

  @NotNull(message = "owner is a required field")
  @JsonProperty(required = true)
  private String owner;

  @NotNull(message = "balance is a required field")
  @JsonProperty(required = true)
  private BigDecimal balance;

  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.ISO_OFFSET_DATE_TIME)
  @Schema(accessMode = READ_ONLY)
  private OffsetDateTime createdAt;


  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.ISO_OFFSET_DATE_TIME)
  @Schema(accessMode = READ_ONLY)
  private OffsetDateTime updatedAt;

  @Schema(accessMode = READ_ONLY)
  private Long version;

}
