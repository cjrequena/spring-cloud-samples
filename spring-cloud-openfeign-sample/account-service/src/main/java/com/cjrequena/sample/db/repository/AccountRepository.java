package com.cjrequena.sample.db.repository;

import com.cjrequena.sample.db.entity.AccountEntity;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Repository
@Transactional
public interface AccountRepository extends CrudRepository<AccountEntity, UUID> {

  @Override
  @Transactional(readOnly = true)
  Optional<AccountEntity> findById(UUID id);

  @Override
  @Transactional(readOnly = true)
  List<AccountEntity> findAll();

  //@Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  @Lock(LockModeType.OPTIMISTIC)
  Optional<AccountEntity> findWithLockingById(UUID id);

  @Modifying
  @Query(value = "INSERT INTO S_ACCOUNT.T_ACCOUNT "
    + " (ID, OWNER, BALANCE, VERSION) "
    + " VALUES (:#{#entity.id}, :#{#entity.owner}, :#{#entity.balance}, 1)"
    , nativeQuery = true)
  void create(@Param("entity") AccountEntity entity);

  @Modifying
  @Query(value = "UPDATE S_ACCOUNT.T_ACCOUNT "
    + " SET OWNER = :#{#entity.owner}, "
    + " BALANCE = :#{#entity.balance} "
    + " VERSION= VERSION + 1"
    + " WHERE ID = :#{#entity.id} AND VERSION = :#{#entity.version}",
    nativeQuery = true)
  void update(@Param("entity") AccountEntity entity);
}
