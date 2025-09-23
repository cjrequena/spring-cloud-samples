package com.cjrequena.sample.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Data
@ToString(callSuper = true)
public class Account implements Serializable {

  private UUID id;
  private String owner;
  private BigDecimal balance;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
  private Long version;

  @Builder
  public Account(UUID id, String owner, BigDecimal balance, OffsetDateTime createdAt, OffsetDateTime updatedAt, Long version) {
    if (Objects.isNull(id)) {
      this.id = UUID.randomUUID();
    } else {
      this.id = id;
    }
    this.owner = owner;
    this.balance = balance;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.version = version;
  }

  public static Account createNewWith(UUID id, String owner, BigDecimal balance) {
    return Account.builder()
      .id(id)
      .owner(owner)
      .balance(balance)
      .build();
  }

  public static Account createNewWith(String owner, BigDecimal balance) {
    return Account.builder()
      .owner(owner)
      .balance(balance)
      .build();
  }
}
