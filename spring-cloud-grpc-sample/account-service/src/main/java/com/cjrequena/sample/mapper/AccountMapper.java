package com.cjrequena.sample.mapper;

import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.persistence.entity.AccountEntity;
import com.cjrequena.sample.proto.Account;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AccountMapper {

  // ========================================
  // Proto <-> Entity Mappings
  // ========================================
  AccountEntity toEntity(Account account);

  Account toAccount(AccountEntity entity);

  // ========================================
  // Domain <-> Entity Mappings
  // ========================================
  com.cjrequena.sample.domain.model.Account toAccountDomain(AccountEntity entity);

  AccountEntity toEntity(com.cjrequena.sample.domain.model.Account account);

  // ========================================
  // DTO <-> Domain Mappings
  // ========================================
  com.cjrequena.sample.domain.model.Account toAccountDomain(AccountDTO dto);

  com.cjrequena.sample.dto.AccountDTO toDTO(com.cjrequena.sample.domain.model.Account account);

  List<com.cjrequena.sample.dto.AccountDTO> toDTOList(List<com.cjrequena.sample.domain.model.Account> accounts);

//  com.cjrequena.sample.domain.model.Account toAccountDomain(com.cjrequena.sample.dto.AccountDTO dto);
//
//  com.cjrequena.sample.openapi.controller.dto.AccountDTO toDTO(com.cjrequena.sample.domain.model.Account account);
//
//  List<com.cjrequena.sample.openapi.controller.dto.AccountDTO> toDTOList(List<com.cjrequena.sample.domain.model.Account> accounts);

  // ========================================
  // Update Mappings
  // ========================================

  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromAccount(com.cjrequena.sample.proto.Account account, @MappingTarget AccountEntity entity);

  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromAccount(com.cjrequena.sample.domain.model.Account account, @MappingTarget AccountEntity entity);

  // Custom conversion: long -> LocalDate
  default LocalDate map(long epochMillis) {
    return Instant.ofEpochMilli(epochMillis)
      .atZone(ZoneId.systemDefault())
      .toLocalDate();
  }

  // Custom conversion: LocalDate -> long
  default long map(LocalDate date) {
    return date.atStartOfDay(ZoneId.systemDefault())
      .toInstant()
      .toEpochMilli();
  }
}
