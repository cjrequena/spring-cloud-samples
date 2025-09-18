package com.cjrequena.sample.service;

import com.cjrequena.sample.common.EStatus;
import com.cjrequena.sample.domain.model.Order;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.*;
import com.cjrequena.sample.mapper.OrderMapper;
import com.cjrequena.sample.persistence.entity.OrderEntity;
import com.cjrequena.sample.persistence.repository.OrderRepository;
import com.cjrequena.sample.proto.Account;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ServiceException.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderService {

  private final OrderMapper orderMapper;
  private final OrderRepository orderRepository;
  private final AccountServiceGrpcClient accountServiceGrpcClient;

  public void create(Order order) throws InsufficientBalanceException {
    Account account = this.accountServiceGrpcClient.retrieveById(order.getAccountId());
    BigDecimal accountBalance = new BigDecimal(account.getBalance()).setScale(2, RoundingMode.HALF_UP);

    BigDecimal amount = accountBalance.subtract(order.getTotal());
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      String errorMessage = String.format("The account :: %s :: has insufficient balance", account.getId());
      throw new InsufficientBalanceException(errorMessage);
    }
    OrderEntity entity = this.orderMapper.toEntity(order);
    this.orderRepository.create(entity);
  }

  @Transactional(readOnly = true)
  public Order retrieveById(UUID id) throws OrderNotFoundException {
    return this.orderRepository
      .findById(id)
      .map(this.orderMapper::toOrderDomain)
      .orElseThrow(() -> {
        String errorMessage = String.format("The order :: %s :: was not found", id);
        if (log.isTraceEnabled()) {
          log.trace(errorMessage);
        }
        return new OrderNotFoundException(errorMessage);
      });
  }

  @Transactional(readOnly = true)
  public List<Order> retrieve() {
    return this.orderRepository.findAll().stream().map(this.orderMapper::toOrderDomain).collect(Collectors.toList());
  }

  public void update(Order order) throws OrderNotFoundException, OptimisticConcurrencyException {

    this.orderRepository
      .findWithLockingById(order.getId())
      .ifPresentOrElse(entity -> {
        try {
          this.orderMapper.updateEntityFromOrderDomain(order, entity);
          this.orderRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException ex) {
          String errorMessage = String.format("Optimistic concurrency control error in order :: %s :: actual version doesn't match expected version", order.getId());
          if (log.isTraceEnabled()) {
            log.trace(errorMessage);
          }
          throw new OptimisticConcurrencyException(errorMessage);
        }
      }, () -> {
        String errorMessage = String.format("The order :: %s :: was not found", order.getId());
        if (log.isTraceEnabled()) {
          log.trace(errorMessage);
        }
        throw new AccountNotFoundException(errorMessage);
      });
  }

  public Order patch(Integer id, JsonPatch patchDocument) {
    return null;
  }

  public Order patch(Integer id, JsonMergePatch mergePatchDocument) {
    return null;
  }

  public void delete(UUID id) throws OrderNotFoundException {
    Optional<OrderEntity> optional = this.orderRepository.findById(id);
    if (optional.isEmpty()) {
      throw new OrderNotFoundException("The order :: " + id + " :: was not Found");
    }
    this.orderRepository.deleteById(id);
  }

  @Scheduled(
    fixedDelayString = "3000",
    initialDelayString = "3000")
  public void processPendingOrders() {
    log.debug("Processing pending orders ");
    List<OrderEntity> orderEntities = this.orderRepository.retrieveOrdersByStatus(EStatus.PENDING.getValue());
    for (OrderEntity orderEntity : orderEntities) {
      log.debug("id {} account_id {} status {}", orderEntity.getId(), orderEntity.getAccountId(), orderEntity.getStatus());
      WithdrawAccountDTO withdrawAccountDTO = new WithdrawAccountDTO();
      withdrawAccountDTO.setAccountId(orderEntity.getAccountId());
      withdrawAccountDTO.setAmount(orderEntity.getTotal());
      try {
        this.accountServiceGrpcClient.withdraw(withdrawAccountDTO);
        orderEntity.setStatus(EStatus.COMPLETED.getValue());
      } catch (Exception ex) {
        orderEntity.setStatus(EStatus.REJECTED.getValue());
        orderEntity.setDescription(ex.getMessage());
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
      this.orderRepository.save(orderEntity);
    }
  }

}
