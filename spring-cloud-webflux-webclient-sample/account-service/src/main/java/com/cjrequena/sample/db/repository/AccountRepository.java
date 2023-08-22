package com.cjrequena.sample.db.repository;

import com.cjrequena.sample.db.entity.AccountEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Repository
@Transactional
public interface AccountRepository extends ReactiveMongoRepository<AccountEntity, UUID> {

}
