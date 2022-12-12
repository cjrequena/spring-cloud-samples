package com.cjrequena.sample.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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

  @Column(name = "creation_date", nullable = false, updatable = false, insertable = false)
  @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
  private LocalDate creationDate;

  @Version
  @Column(name = "version")
  private Long version;
}
