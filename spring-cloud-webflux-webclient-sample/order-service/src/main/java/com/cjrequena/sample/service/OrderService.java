package com.cjrequena.sample.service;

import com.cjrequena.sample.common.EStatus;
import com.cjrequena.sample.domain.model.Order;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.*;
import com.cjrequena.sample.mapper.OrderMapper;
import com.cjrequena.sample.persistence.entity.OrderEntity;
import com.cjrequena.sample.persistence.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 */
@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ServiceException.class)
public class OrderService {

  private final OrderMapper orderMapper;
  private final OrderRepository orderRepository;
  private final AccountService accountService;

  public Mono<Order> create(Order order) {
    return accountService.retrieve(order.getAccountId())
      .flatMap(response -> {
        AccountDTO accountDTO = response.getBody();
        if (accountDTO == null) {
          return Mono.error(new AccountNotFoundException("Account " + order.getAccountId() + " not found"));
        }

        BigDecimal newBalance = accountDTO.getBalance().subtract(order.getTotal());
        if (newBalance.signum() < 0) {
          return Mono.error(new InsufficientBalanceException("Insufficient balance for account " + accountDTO.getId()));
        }

        OrderEntity entity = orderMapper.toEntity(order);
        entity.setId(UUID.randomUUID());

        return orderRepository.save(entity).map(orderMapper::toOrderDomain);
      });
  }




  public Mono<Order> retrieveById(UUID id) {
    return orderRepository
      .findById(id)
      .switchIfEmpty(Mono.error(new OrderNotFoundException("The order :: " + id + " :: was not Found")))
      .map(this.orderMapper::toOrderDomain);
  }

  public Flux<Order> retrieve() {
    return this.orderRepository
      .findAll()
      .map(this.orderMapper::toOrderDomain);
  }

  public Mono<OrderEntity> update(Order order) {
    UUID orderId = order.getId();
    log.debug("Updating order with id={}", orderId);
    return orderRepository.findById(orderId)
      .switchIfEmpty(Mono.error(() -> new OrderNotFoundException("The order :: " + orderId + " :: was not Found")))
      .flatMap(existingEntity -> {
        long expectedVersion = existingEntity.getVersion();
        orderMapper.updateEntityFromOrderDomain(order, existingEntity);
        return orderRepository.save(existingEntity)
          .switchIfEmpty(Mono.error(() -> {
            String errorMessage = String.format(
              "Optimistic concurrency control error for order %s: expected version=%s but version mismatch",
              orderId, expectedVersion
            );
            log.warn(errorMessage);
            return new OptimisticConcurrencyException(errorMessage);
          }))
          .doOnSuccess(saved -> log.info("Order {} updated successfully, version {} → {}",
            orderId, expectedVersion, saved.getVersion()))
          .doOnError(error -> log.error("Failed to update order :: {} :: {}", orderId, error.getMessage(), error));
      });
  }

  public Mono<Void> delete(UUID id) {
    return this.orderRepository
      .findById(id)
      .switchIfEmpty(Mono.error(new OrderNotFoundException("The order :: " + id + " :: was not Found")))
      .flatMap(entity -> this.orderRepository.deleteById(entity.getId()));
  }

  @Scheduled(
    fixedDelayString = "3000",
    initialDelayString = "3000")
  public void processPendingOrders() {
    log.debug("Processing pending orders ");
    this.orderRepository
      .findByStatusOrderByCreationDateDesc(EStatus.PENDING.getValue())
      .doOnNext(orderEntity -> {
        log.debug("id {} account_id {} status {} creation_date {}", orderEntity.getId(), orderEntity.getAccountId(), orderEntity.getStatus(), orderEntity.getCreationDate());
        WithdrawAccountDTO withdrawAccountDTO = new WithdrawAccountDTO();
        withdrawAccountDTO.setAccountId(orderEntity.getAccountId());
        withdrawAccountDTO.setAmount(orderEntity.getTotal());
        this.accountService
          .withdraw(withdrawAccountDTO)
          .doOnNext(response -> {
            if (response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
              orderEntity.setStatus(EStatus.COMPLETED.getValue());
              this.update(this.orderMapper.toOrderDomain(orderEntity))
                .doOnNext(log::trace)
                .subscribe();
            }
          })
          .doOnError(ex -> {
            log.error(ex.getMessage());
            if (ex instanceof InsufficientBalanceException) {
              orderEntity.setStatus(EStatus.REJECTED.getValue());
              orderEntity.setDescription(ex.getLocalizedMessage());
              this.update(this.orderMapper.toOrderDomain(orderEntity))
                .doOnNext(log::trace)
                .subscribe();
            }
          })
          .onErrorResume(Mono::error)
          .subscribe();
      })
      .doOnError(ex ->{
        log.error(ex.getMessage());
      })
      .doOnComplete(()->{
        log.info("Processed pending orders");
      })
      .onErrorResume(Mono::error)
      .subscribe();
  }
}
