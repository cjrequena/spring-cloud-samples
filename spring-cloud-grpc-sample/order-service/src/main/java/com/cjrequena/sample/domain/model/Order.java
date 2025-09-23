package com.cjrequena.sample.domain.model;

import com.cjrequena.sample.common.EStatus;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Data
@ToString(callSuper = true)
public class Order {

  private UUID id;
  private UUID accountId;
  private String description;
  private BigDecimal total;
  private EStatus status = EStatus.PENDING; // Default value
  private LocalDate createdAt;
  private LocalDate updatedAt;
  private Long version;

  @Builder
  public Order(UUID id, UUID accountId, String description, BigDecimal total, EStatus status,  LocalDate createdAt, LocalDate updatedAt, Long version) {
    if (Objects.isNull(id)) {
      this.id = UUID.randomUUID();
    } else {
      this.id = id;
    }
    this.accountId = accountId;
    this.description = description;
    this.total = total;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.version = version;
  }
}
