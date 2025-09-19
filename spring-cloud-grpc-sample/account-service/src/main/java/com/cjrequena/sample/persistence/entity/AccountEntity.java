package com.cjrequena.sample.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_account")
public class AccountEntity {
  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "owner")
  private String owner;

  @Column(name = "balance")
  private BigDecimal balance;

  @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
  @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, updatable = false, insertable = false)
  @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
  private LocalDateTime updatedAt;

  @Version
  @Column(name = "version")
  private Long version;
}
