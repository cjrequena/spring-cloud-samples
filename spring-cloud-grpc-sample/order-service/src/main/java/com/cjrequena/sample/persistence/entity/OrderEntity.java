package com.cjrequena.sample.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_order")
public class OrderEntity {

  @Id
  @Column(name = "id")
  UUID id;

  @Column(name = "account_id")
  UUID accountId;

  @Column(name = "total")
  BigDecimal total;

  @Column(name = "status")
  String status;

  @Column(name = "description")
  String description;

  @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
  //@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false, updatable = false, insertable = false)
  //@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
  private OffsetDateTime updatedAt;

  @Version
  @Column(name = "version")
  private Long version;
}
