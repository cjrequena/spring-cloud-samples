package com.cjrequena.sample.persistence.repository;

import com.cjrequena.sample.persistence.entity.AccountEntity;
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
