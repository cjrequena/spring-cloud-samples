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

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("account")
public class AccountEntity {
  @Id
  private UUID id;

  private String owner;

  private BigDecimal balance;

  @Field(value = "creation_date")
  private LocalDate creationDate;

  @Version
  private Long version;
}
