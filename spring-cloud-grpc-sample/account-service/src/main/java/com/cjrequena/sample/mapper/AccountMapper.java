package com.cjrequena.sample.mapper;

import com.cjrequena.sample.db.entity.AccountEntity;
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

  AccountEntity toEntity(Account account);

  Account toAccount(AccountEntity entity);
}
