package com.cjrequena.sample.service;

import com.cjrequena.sample.db.entity.OrderEntity;
import com.cjrequena.sample.db.repository.OrderRepository;
import com.cjrequena.sample.dto.OrderDTO;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.exception.service.OrderNotFoundServiceException;
import com.cjrequena.sample.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
public class OrderService {

  private final OrderMapper accountMapper;
  private final OrderRepository accountRepository;

  public Mono<OrderDTO> create(OrderDTO dto) {
    OrderEntity entity = this.accountMapper.toEntity(dto);
    entity.setId(UUID.randomUUID());
    Mono<OrderDTO> dto$ = this.accountRepository.save(entity).map(this.accountMapper::toDTO);
    return dto$;
  }

  public Mono<OrderDTO> retrieveById(UUID id) {
    return accountRepository
      .findById(id)
      .switchIfEmpty(Mono.error(new OrderNotFoundServiceException("The account :: " + id + " :: was not Found")))
      .map(this.accountMapper::toDTO);
  }

  public Flux<OrderDTO> retrieve() {
    Flux<OrderDTO> dtos$ = this.accountRepository.findAll().map(this.accountMapper::toDTO);
    return dtos$;
  }

  public Mono<OrderEntity> update(OrderDTO dto) {
    return accountRepository
      .findById(dto.getId())
      .switchIfEmpty(Mono.error(new OrderNotFoundServiceException("The account :: " + dto.getId() + " :: was not Found")))
      .map(Optional::of)
      .flatMap(optionalOrder -> {
        if (optionalOrder.isPresent()) {
          OrderEntity _entity = optionalOrder.get();
          if (_entity.getVersion().equals(dto.getVersion())) {
            return accountRepository.save(this.accountMapper.toEntity(dto));
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
    return this.accountRepository
      .findById(id)
      .switchIfEmpty(Mono.error(new OrderNotFoundServiceException("The account :: " + id + " :: was not Found")))
      .flatMap(entity -> this.accountRepository.deleteById(entity.getId()));
  }

}
