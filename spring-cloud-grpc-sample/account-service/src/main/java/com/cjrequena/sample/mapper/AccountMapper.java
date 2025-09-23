package com.cjrequena.sample.mapper;

import com.cjrequena.sample.persistence.entity.AccountEntity;
import org.mapstruct.*;

import java.time.*;
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
  AccountEntity toEntity(com.cjrequena.sample.proto.Account account);

  com.cjrequena.sample.proto.Account toAccountProto(AccountEntity entity);

  // ========================================
  // Domain <-> Entity Mappings
  // ========================================
  com.cjrequena.sample.domain.model.Account toAccountDomain(AccountEntity entity);

  AccountEntity toEntity(com.cjrequena.sample.domain.model.Account account);

  // ========================================
  // DTO <-> Domain Mappings
  // ========================================
  com.cjrequena.sample.domain.model.Account toAccountDomain(com.cjrequena.sample.dto.AccountDTO dto);

  com.cjrequena.sample.dto.AccountDTO toDTO(com.cjrequena.sample.domain.model.Account account);

  List<com.cjrequena.sample.dto.AccountDTO> toDTOList(List<com.cjrequena.sample.domain.model.Account> accounts);

  com.cjrequena.sample.domain.model.Account toAccountDomain(com.cjrequena.sample.openapi.controller.dto.AccountDTO dto);

  com.cjrequena.sample.openapi.controller.dto.AccountDTO toOpenApiDTO(com.cjrequena.sample.domain.model.Account account);

  List<com.cjrequena.sample.openapi.controller.dto.AccountDTO> toOpenApiDTODTOList(List<com.cjrequena.sample.domain.model.Account> accounts);

  // ========================================
  // Update Mappings
  // ========================================

  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromAccount(com.cjrequena.sample.proto.Account account, @MappingTarget AccountEntity entity);

  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromAccount(com.cjrequena.sample.domain.model.Account account, @MappingTarget AccountEntity entity);

  default LocalDate mapToLocalDate(long epochMillis) {
    return Instant.ofEpochMilli(epochMillis)
      .atZone(ZoneId.systemDefault())
      .toLocalDate();
  }

  default long mapFromLocalDate(LocalDate date) {
    return date.atStartOfDay(ZoneId.systemDefault())
      .toInstant()
      .toEpochMilli();
  }

  default OffsetDateTime mapToOffsetDateTime(long value) {
    return Instant.ofEpochMilli(value).atOffset(ZoneOffset.UTC);
  }

  default long mapFromOffsetDateTime(OffsetDateTime value) {
    return value.toInstant().toEpochMilli();
  }
}
