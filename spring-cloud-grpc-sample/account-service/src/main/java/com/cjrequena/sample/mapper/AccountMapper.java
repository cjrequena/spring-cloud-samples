package com.cjrequena.sample.mapper;

import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.persistence.entity.AccountEntity;
import com.cjrequena.sample.proto.Account;
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

  AccountDTO toDTO(com.cjrequena.sample.domain.model.Account account);

  List<AccountDTO> toDTOList(List<com.cjrequena.sample.domain.model.Account> accounts);

  // ========================================
  // Update Mappings
  // ========================================

  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromAccount(Account account, @MappingTarget AccountEntity entity);

  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromAccount(com.cjrequena.sample.domain.model.Account account, @MappingTarget AccountEntity entity);
}
