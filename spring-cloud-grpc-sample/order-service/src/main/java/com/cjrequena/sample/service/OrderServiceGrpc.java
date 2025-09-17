package com.cjrequena.sample.service;

import com.cjrequena.sample.common.EStatus;
import com.cjrequena.sample.exception.GrpcExceptionHandler;
import com.cjrequena.sample.exception.service.*;
import com.cjrequena.sample.mapper.OrderMapper;
import com.cjrequena.sample.persistence.entity.OrderEntity;
import com.cjrequena.sample.persistence.repository.OrderRepository;
import com.cjrequena.sample.proto.*;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@GrpcService
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ServiceException.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceGrpc extends com.cjrequena.sample.proto.OrderServiceGrpc.OrderServiceImplBase {

  private final OrderMapper orderMapper;
  private final OrderRepository orderRepository;
  private final AccountServiceGrpcClient accountServiceGrpcClient;
  private final GrpcExceptionHandler grpcExceptionHandler;

  @Override
  public void createOrder(CreateOrderRequest request, StreamObserver<CreateOrderResponse> responseObserver) {
    try {
      Order order = Order
        .newBuilder()
        .setId(UUID.randomUUID().toString())
        .setAccountId(request.getOrder().getAccountId())
        .setTotal(request.getOrder().getTotal())
        .setStatus(EStatus.PENDING.getValue())
        .build();
      Objects.requireNonNull(order, "Order cannot be null");
      Objects.requireNonNull(order.getAccountId(), "AccountId cannot be null");
      Objects.requireNonNull(order.getTotal(), "Order total cannot be null");
      UUID accountId = UUID.fromString(order.getAccountId());
      BigDecimal total = new BigDecimal(order.getTotal()).setScale(2, RoundingMode.HALF_UP);
      Account account = this.accountServiceGrpcClient.retrieveById(accountId);
      BigDecimal accountBalance = new BigDecimal(account.getBalance()).setScale(2, RoundingMode.HALF_UP);
      BigDecimal newAccountBalance = accountBalance.subtract(total);

      if (newAccountBalance.compareTo(BigDecimal.ZERO) < 0) {
        String errorMessage = String.format("The account :: %s :: has insufficient balance", accountId);
        StatusRuntimeException ex = this.grpcExceptionHandler.buildErrorResponse(new AccountNotFoundException(errorMessage));
        responseObserver.onError(ex);
      }

      final OrderEntity entity = this.orderMapper.toEntity(order);
      this.orderRepository.create(entity);

      CreateOrderResponse response = CreateOrderResponse
        .newBuilder()
        .setSuccess(true)
        .setMessage("Order created successfully")
        .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (AccountNotFoundException | AccountServiceUnavailableException ex) {
      StatusRuntimeException err = this.grpcExceptionHandler.buildErrorResponse(ex);
      responseObserver.onError(err);
    }
  }

  @Override
  public void retrieveOrderById(RetrieveOrderByIdRequest request, StreamObserver<RetrieveOrderByIdResponse> responseObserver) {
    UUID orderId = UUID.fromString(request.getId());
    this.orderRepository.findById(orderId)
      .map(this.orderMapper::toOrder)
      .map(order -> RetrieveOrderByIdResponse.newBuilder().setOrder(order).build())
      .ifPresentOrElse(response -> {
          responseObserver.onNext(response);
          responseObserver.onCompleted();
        },
        () -> {
          String errorMessage = String.format("The order :: %s :: was not found", orderId);
          final StatusRuntimeException err = this.grpcExceptionHandler.buildErrorResponse(new OrderNotFoundException(errorMessage));
          responseObserver.onError(err);
        }
      );
  }

  @Override
  public void retrieveOrders(RetrieveOrdersRequest request, StreamObserver<RetrieveOrdersResponse> responseObserver) {
    final List<Order> orderList = this.orderRepository.findAll().stream().map(this.orderMapper::toOrder).toList();
    final RetrieveOrdersResponse response = RetrieveOrdersResponse.newBuilder().addAllOrders(orderList).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updateOrder(UpdateOrderRequest request, StreamObserver<UpdateOrderResponse> responseObserver) {
    Order order = request.getOrder();
    UUID orderId = UUID.fromString(order.getId());
    this.orderRepository
      .findWithLockingById(orderId)
      .ifPresentOrElse(orderEntity -> {
          final long expectedVersion = orderEntity.getVersion();
          try {
            this.orderMapper.updateEntityFromOrder(order, orderEntity);
            this.orderRepository.save(orderEntity);
            UpdateOrderResponse response = UpdateOrderResponse
              .newBuilder()
              .setSuccess(true)
              .setMessage("Order updated successfully")
              .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
          } catch (ObjectOptimisticLockingFailureException ex) {
            String errorMessage = String.format(
              "Optimistic concurrency control error in order :: %s :: actual version doesn't match expected version %s",
              orderId, expectedVersion
            );
            log.trace(errorMessage);
            StatusRuntimeException err = this.grpcExceptionHandler.buildErrorResponse(new OptimisticConcurrencyException(errorMessage));
            responseObserver.onError(err);
          }
        },
        () -> {
          String errorMessage = String.format("The order :: %s :: was not found", orderId);
          log.trace(errorMessage);
          StatusRuntimeException err = this.grpcExceptionHandler.buildErrorResponse(new OrderNotFoundException(errorMessage));
          responseObserver.onError(err);
        }
      );
  }

  @Override
  public void deleteOrder(DeleteOrderRequest request, StreamObserver<DeleteOrderResponse> responseObserver) {
    UUID orderId = UUID.fromString(request.getId());
    this.orderRepository
      .findById(orderId)
      .ifPresentOrElse(
        this.orderRepository::delete,
        () -> {
          String errorMessage = String.format("The order :: %s :: was not found", orderId);
          final StatusRuntimeException err = this.grpcExceptionHandler.buildErrorResponse(new OrderNotFoundException(errorMessage));
          responseObserver.onError(err);
        }
      );

    DeleteOrderResponse response = DeleteOrderResponse.newBuilder().setSuccess(true).setMessage("Order deleted successfully").build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

}
