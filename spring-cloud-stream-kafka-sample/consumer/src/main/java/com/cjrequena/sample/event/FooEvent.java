package com.cjrequena.sample.event;

import com.cjrequena.sample.dto.FooDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class FooEvent implements Serializable {

  // Unique id for the specific message. This id is globally unique
  @JsonProperty(value = "id")
  @NotNull(message = "id is mandatory")
  @Pattern(regexp = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", message = "Format is not valid")
  @Getter(onMethod = @__({@JsonProperty("id")}))
  protected String id;

  // Identifies the context in which an event happened.
  protected String source;

  // The version of the CloudEvents specification which the event uses.
  @JsonProperty(value = "specversion")
  @NotNull(message = "specversion is required")
  @Getter(onMethod = @__({@JsonProperty("specversion")}))
  protected final String specVersion = "1.0";

  // Type of message
  protected String type;

  // Content type of the data value. Must adhere to RFC 2046 format.
  @JsonProperty(value = "datacontenttype")
  @NotNull(message = "datacontenttype is required")
  @Getter(onMethod = @__({@JsonProperty("datacontenttype")}))
  public String dataContentType;

  // Describes the subject of the event in the context of the event producer (identified by source).
  protected String subject;

  // Date and time for when the message was published
  protected OffsetDateTime time;

  // The event payload.
  protected FooDTO data;

  // Base64 encoded event payload. Must adhere to RFC4648.
  protected String dataBase64;

  // Identifies the schema that data adheres to.
  protected String dataSchema;
}
