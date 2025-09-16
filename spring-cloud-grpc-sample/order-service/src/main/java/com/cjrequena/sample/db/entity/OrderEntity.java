package com.cjrequena.sample.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

//  @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
//  @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
//  private LocalDate createdAt;
//
//  @Column(name = "updated_at", nullable = false, updatable = false, insertable = false)
//  @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
//  private LocalDate updatedAt;

  @Version
  @Column(name = "version")
  private Long version;
}
