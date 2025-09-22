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
  private BigDecimal total;
  private LocalDate creationDate;
  private EStatus status = EStatus.PENDING; // Default value
  private String description;
  private Long version;

  @Builder
  public Order(UUID id, UUID accountId, BigDecimal total, LocalDate creationDate, EStatus status, String description, Long version) {
    if (Objects.isNull(id)) {
      this.id = UUID.randomUUID();
    } else {
      this.id = id;
    }
    this.accountId = accountId;
    this.total = total;
    this.creationDate = creationDate;
    this.status = status;
    this.description = description;
    this.version = version;
  }
}
