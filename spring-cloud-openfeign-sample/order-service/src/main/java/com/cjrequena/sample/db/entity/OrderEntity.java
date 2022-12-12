package com.cjrequena.sample.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_order")
public class OrderEntity {

  @Id
  @Column(name = "id")
  Integer id;

  @Column(name = "account_id")
  UUID accountId;

  @Column(name = "total")
  BigDecimal total;

  @Column(name = "creation_date", nullable = false, updatable = false, insertable = false)
  @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
  private LocalDate creationDate;

  @Column(name = "status")
  String status;

  @Column(name = "description")
  String description;

  @Version
  @Column(name = "version")
  private Long version;
}
