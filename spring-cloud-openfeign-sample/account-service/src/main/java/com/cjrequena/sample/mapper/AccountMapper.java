package com.cjrequena.sample.mapper;

import com.cjrequena.sample.db.entity.AccountEntity;
import com.cjrequena.sample.dto.AccountDTO;
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

  AccountEntity toEntity(AccountDTO dto);

  AccountDTO toDTO(AccountEntity entity);

  // ========================================
  // Update Mappings
  // ========================================

  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromAccount(AccountDTO dto, @MappingTarget AccountEntity entity);
}
