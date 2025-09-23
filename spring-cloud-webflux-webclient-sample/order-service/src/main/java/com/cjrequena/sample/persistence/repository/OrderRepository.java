package com.cjrequena.sample.persistence.repository;

import com.cjrequena.sample.persistence.entity.OrderEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Repository
@Transactional
public interface OrderRepository extends ReactiveMongoRepository<OrderEntity, UUID> {


   Flux<OrderEntity> findByStatusOrderByCreationDateDesc(@Param("status") String status);

//  @Override
//  @Transactional(readOnly = true)
//  Optional<OrderEntity> findById(Integer id);
//
//  @Override
//  @Transactional(readOnly = true)
//  List<OrderEntity> findAll();
//
//  @Lock(LockModeType.OPTIMISTIC)
//  Optional<OrderEntity> findWithLockingById(Integer id);
//
//  @Query(value = "SELECT * FROM S_ORDER.T_ORDER WHERE STATUS = :status ORDER BY CREATION_DATE DESC", nativeQuery = true)
//  List<OrderEntity> retrieveOrdersByStatus(@Param("status") String status);
//
//  @Modifying
//  @Transactional
//  @Query(value = "INSERT INTO S_ORDER.T_ORDER "
//    + " (ACCOUNT_ID, STATUS, TOTAL, VERSION) "
//    + " VALUES (:#{#entity.accountId}, :#{#entity.status},:#{#entity.total}, 1)"
//    , nativeQuery = true)
//  void create(@Param("entity") OrderEntity entity);
//
//  @Modifying
//  @Transactional
//  @Query(value = "UPDATE S_ORDER.T_ORDER "
//    + " SET ACCOUNT_ID = :#{#entity.accountId}, "
//    + " STATUS = :#{#entity.status}, "
//    + " TOTAL = :#{#entity.total}, "
//    + " VERSION= VERSION + 1"
//    + " WHERE ID = :#{#entity.id} AND VERSION = :#{#entity.version}",
//    nativeQuery = true)
//  void update(@Param("entity") OrderEntity entity);
}
