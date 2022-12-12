package com.cjrequena.sample.service;

import com.cjrequena.sample.common.EStatus;
import com.cjrequena.sample.db.entity.OrderEntity;
import com.cjrequena.sample.db.repository.OrderRepository;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.OrderDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.*;
import com.cjrequena.sample.mapper.OrderMapper;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ServiceException.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderService {

  private final OrderMapper orderMapper;
  private final OrderRepository orderRepository;
  private final AccountService accountService;

  public void create(OrderDTO dto) throws FeignServiceException, InsufficientBalanceServiceException {
    AccountDTO accountDTO = this.accountService.retrieve(dto.getAccountId());
    BigDecimal amount = accountDTO.getBalance().subtract(dto.getTotal());
    if (amount.compareTo(BigDecimal.ZERO) == -1) {
      throw new InsufficientBalanceServiceException("Insufficient balance on account with id " + accountDTO.getId());
    }
    OrderEntity entity = this.orderMapper.toEntity(dto);
    this.orderRepository.create(entity);
  }

  @Transactional(readOnly = true)
  public OrderDTO retrieveById(Integer id) throws OrderNotFoundServiceException {
    Optional<OrderEntity> optional = this.orderRepository.findById(id);
    if (optional.isEmpty()) {
      throw new OrderNotFoundServiceException("The order :: " + id + " :: was not Found");
    }
    return orderMapper.toDTO(optional.get());
  }

  @Transactional(readOnly = true)
  public List<OrderDTO> retrieve() {
    return this.orderRepository.findAll().stream().map(this.orderMapper::toDTO).collect(Collectors.toList());
  }

  public void update(OrderDTO dto) throws OrderNotFoundServiceException, OptimisticConcurrencyServiceException {
    Optional<OrderEntity> optional = this.orderRepository.findWithLockingById(dto.getId());
    if (optional.isEmpty()) {
      throw new OrderNotFoundServiceException("The order :: " + dto.getId() + " :: was not Found");
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
      throw new OptimisticConcurrencyServiceException(
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

  public void delete(Integer id) throws OrderNotFoundServiceException {
    Optional<OrderEntity> optional = this.orderRepository.findById(id);
    if (optional.isEmpty()) {
      throw new OrderNotFoundServiceException("The order :: " + id + " :: was not Found");
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
      log.debug("id {} account_id {} status {} creation_date {}", orderEntity.getId(), orderEntity.getAccountId(), orderEntity.getStatus(), orderEntity.getCreationDate());
      WithdrawAccountDTO withdrawAccountDTO = new WithdrawAccountDTO();
      withdrawAccountDTO.setAccountId(orderEntity.getAccountId());
      withdrawAccountDTO.setAmount(orderEntity.getTotal());
      try {
        this.accountService.withdraw(withdrawAccountDTO);
        orderEntity.setStatus(EStatus.COMPLETED.getValue());
      } catch (FeignServiceException ex) {
        orderEntity.setStatus(EStatus.REJECTED.getValue());
        orderEntity.setDescription(ex.getMessage());
      }
      this.orderRepository.save(orderEntity);
    }
  }

}
