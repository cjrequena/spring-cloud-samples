package com.cjrequena.sample.service;

import com.cjrequena.sample.common.EStatus;
import com.cjrequena.sample.dto.OrderDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.InsufficientBalanceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyException;
import com.cjrequena.sample.exception.service.OrderNotFoundException;
import com.cjrequena.sample.exception.service.ServiceException;
import com.cjrequena.sample.mapper.OrderMapper;
import com.cjrequena.sample.persistence.entity.OrderEntity;
import com.cjrequena.sample.persistence.repository.OrderRepository;
import com.cjrequena.sample.proto.Account;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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

  public void create(OrderDTO dto) throws InsufficientBalanceException {
    Account account = this.accountServiceGrpcClient.retrieveById(dto.getAccountId());
    BigDecimal accountBalance = new BigDecimal(account.getBalance()).setScale(2, RoundingMode.HALF_UP);

    BigDecimal amount = accountBalance.subtract(dto.getTotal());
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new InsufficientBalanceException("Insufficient balance on account with id " + account.getId());
    }
    OrderEntity entity = this.orderMapper.toEntity(dto);
    this.orderRepository.create(entity);
  }

  @Transactional(readOnly = true)
  public OrderDTO retrieveById(UUID id) throws OrderNotFoundException {
    Optional<OrderEntity> optional = this.orderRepository.findById(id);
    if (optional.isEmpty()) {
      throw new OrderNotFoundException("The order :: " + id + " :: was not Found");
    }
    return orderMapper.toDTO(optional.get());
  }

  @Transactional(readOnly = true)
  public List<OrderDTO> retrieve() {
    return this.orderRepository.findAll().stream().map(this.orderMapper::toDTO).collect(Collectors.toList());
  }

  public void update(OrderDTO dto) throws OrderNotFoundException, OptimisticConcurrencyException {
    Optional<OrderEntity> optional = this.orderRepository.findWithLockingById(dto.getId());
    if (optional.isEmpty()) {
      throw new OrderNotFoundException("The order :: " + dto.getId() + " :: was not Found");
    }
    OrderDTO _dto = this.orderMapper.toDTO(optional.get());
    if (_dto.getVersion().equals(dto.getVersion())) {
      OrderEntity entity = this.orderMapper.toEntity(dto);
      this.orderRepository.save(entity);
      log.debug("Updated order with id {}", entity.getId());
    } else {
      log.trace(
        "Optimistic concurrency control error in order :: {} :: actual version doesn't match expected version {}",
        _dto.getId(),
        _dto.getVersion());
      throw new OptimisticConcurrencyException(
        "Optimistic concurrency control error in order :: " + _dto.getId() + " :: actual version doesn't match expected version "
          + _dto.getVersion());
    }
  }

  public OrderDTO patch(Integer id, JsonPatch patchDocument) {
    return null;
  }

  public OrderDTO patch(Integer id, JsonMergePatch mergePatchDocument) {
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
