package com.cjrequena.sample.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Data
@ToString(callSuper = true)
public class DepositAccount {

  private UUID accountId;
  private BigDecimal amount;

  @Builder
  public DepositAccount(UUID accountId, BigDecimal amount) {
    this.accountId = accountId;
    this.amount = amount;
  }
}
