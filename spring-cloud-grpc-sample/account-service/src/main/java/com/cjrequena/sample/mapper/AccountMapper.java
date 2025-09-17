package com.cjrequena.sample.mapper;

import com.cjrequena.sample.persistence.entity.AccountEntity;
import com.cjrequena.sample.proto.Account;
import org.mapstruct.*;

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

  AccountEntity toEntity(Account account);

  Account toAccount(AccountEntity entity);

  // ========================================
  // Update Mappings
  // ========================================

  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromAccount(Account account, @MappingTarget AccountEntity entity);
}
