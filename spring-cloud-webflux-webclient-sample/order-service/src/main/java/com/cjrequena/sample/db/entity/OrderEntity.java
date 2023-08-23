package com.cjrequena.sample.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("order")
public class OrderEntity {

  @Id
  UUID id;

  @Field(value = "account_id")
  UUID accountId;

  BigDecimal total;

  @Field(value = "creation_date")
  private LocalDate creationDate;

  String status;

  String description;

  @Version
  private Long version;
}
