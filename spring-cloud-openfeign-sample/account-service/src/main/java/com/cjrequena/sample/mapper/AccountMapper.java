package com.cjrequena.sample.mapper;

import com.cjrequena.sample.db.entity.AccountEntity;
import com.cjrequena.sample.dto.AccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

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

}
