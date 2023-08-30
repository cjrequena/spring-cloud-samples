package com.cjrequena.sample.service;

import com.cjrequena.sample.common.EStatus;
import com.cjrequena.sample.db.entity.OrderEntity;
import com.cjrequena.sample.db.repository.OrderRepository;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.OrderDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.InsufficientBalanceServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.exception.service.OrderNotFoundServiceException;
import com.cjrequena.sample.exception.service.ServiceException;
import com.cjrequena.sample.mapper.OrderMapper;
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
import java.util.Optional;
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

  public Mono<OrderDTO> create(OrderDTO dto) {

    return accountService
      .retrieve(dto.getAccountId())
      .doOnNext(log::trace)
      .flatMap(response -> {
        AccountDTO accountDTO = response.getBody();
        BigDecimal amount = accountDTO.getBalance().subtract(dto.getTotal());
        if (amount.compareTo(BigDecimal.ZERO) == -1) {
          return Mono.error(new InsufficientBalanceServiceException("Insufficient balance on account with id " + accountDTO.getId()));
        }
        OrderEntity entity = this.orderMapper.toEntity(dto);
        entity.setId(UUID.randomUUID());
        return this.orderRepository.save(entity).map(this.orderMapper::toDTO);
      });
  }

  public Mono<OrderDTO> retrieveById(UUID id) {
    return orderRepository
      .findById(id)
      .switchIfEmpty(Mono.error(new OrderNotFoundServiceException("The account :: " + id + " :: was not Found")))
      .map(this.orderMapper::toDTO);
  }

  public Flux<OrderDTO> retrieve() {
    Flux<OrderDTO> dtos$ = this.orderRepository.findAll().map(this.orderMapper::toDTO);
    return dtos$;
  }

  public Mono<OrderEntity> update(OrderDTO dto) {
    return orderRepository
      .findById(dto.getId())
      .switchIfEmpty(Mono.error(new OrderNotFoundServiceException("The account :: " + dto.getId() + " :: was not Found")))
      .map(Optional::of)
      .flatMap(optionalOrder -> {
        if (optionalOrder.isPresent()) {
          OrderEntity _entity = optionalOrder.get();
          if (_entity.getVersion().equals(dto.getVersion())) {
            return orderRepository.save(this.orderMapper.toEntity(dto));
          } else {
            log.trace(
              "Optimistic concurrency control error in account :: {} :: actual version doesn't match expected version {}",
              _entity.getId(),
              _entity.getVersion());
            return Mono.error(new OptimisticConcurrencyServiceException(
              "Optimistic concurrency control error in account :: " + _entity.getId() + " :: actual version doesn't match expected version "
                + _entity.getVersion()));
          }
        }
        return Mono.empty();
      });
  }

  public Mono<Void> delete(UUID id) {
    return this.orderRepository
      .findById(id)
      .switchIfEmpty(Mono.error(new OrderNotFoundServiceException("The account :: " + id + " :: was not Found")))
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
              this.update(this.orderMapper.toDTO(orderEntity))
                .doOnNext(log::trace)
                .subscribe();
            }
          })
          .doOnError(ex -> {
            log.error(ex.getMessage());
            if (ex instanceof InsufficientBalanceServiceException) {
              orderEntity.setStatus(EStatus.REJECTED.getValue());
              orderEntity.setDescription(ex.getLocalizedMessage());
              this.update(this.orderMapper.toDTO(orderEntity))
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
