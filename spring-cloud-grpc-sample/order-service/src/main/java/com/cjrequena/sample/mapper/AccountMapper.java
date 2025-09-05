package com.cjrequena.sample.mapper;

import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.proto.Account;
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

  AccountDTO toDTO(Account account);

  Account toAccount(AccountDTO dto);
}
