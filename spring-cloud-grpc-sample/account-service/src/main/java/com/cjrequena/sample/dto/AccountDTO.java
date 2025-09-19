package com.cjrequena.sample.dto;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.serializer.LocalDateTimeDeserializer;
import com.cjrequena.sample.dto.serializer.LocalDateTimeSerializer;
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
import java.time.LocalDateTime;
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

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
  @Schema(accessMode = READ_ONLY)
  private LocalDateTime createdAt;


  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
  @Schema(accessMode = READ_ONLY)
  private LocalDateTime updatedAt;

  @Schema(accessMode = READ_ONLY)
  private Long version;

}
