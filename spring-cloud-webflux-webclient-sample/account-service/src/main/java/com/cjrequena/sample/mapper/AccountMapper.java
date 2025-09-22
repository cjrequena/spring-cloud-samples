package com.cjrequena.sample.mapper;

import com.cjrequena.sample.persistence.entity.AccountEntity;
import org.mapstruct.*;

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

  //  com.cjrequena.sample.domain.model.Account toAccountDomain(com.cjrequena.sample.openapi.rest.dto.AccountDTO dto);
  //
  //  com.cjrequena.sample.openapi.rest.dto.AccountDTO toDTO(com.cjrequena.sample.domain.model.Account account);
  //
  //  List<com.cjrequena.sample.openapi.rest.dto.AccountDTO> toDTOList(List<com.cjrequena.sample.domain.model.Account> accounts);

  // ========================================
  // Update Mappings
  // ========================================
  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromAccount(com.cjrequena.sample.domain.model.Account account, @MappingTarget AccountEntity entity);

}
