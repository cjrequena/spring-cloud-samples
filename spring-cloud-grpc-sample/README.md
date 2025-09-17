# spring-cloud-grpc-sample

## Create new account calling account-service via gRPC
```bash
grpcurl --plaintext \
  -d '{"account": {"owner": "Alice", "balance": "1000.00"}}' \
  localhost:18080 com.cjrequena.sample.grpc.service.AccountService/CreateAccount
```

## Retrieve account by ID calling account-service via gRPC
```bash
grpcurl --plaintext \
  -d '{"id": "37c0c17c-d9c0-4a92-848f-2a9179221400"}' \
  localhost:18080 com.cjrequena.sample.grpc.service.AccountService/RetrieveAccountById
```

## Retrieve accounts calling account-service via gRPC
```bash
grpcurl --plaintext \
  localhost:18080 com.cjrequena.sample.grpc.service.AccountService/RetrieveAccounts
```

## Update account calling account-service via gRPC
```bash
grpcurl --plaintext \
  -d '{"account": {"id":"b9214d25-8842-4ef0-b5e1-6c1f52cd470f", "owner": "Carlos", "balance": "350.00"}}' \
  localhost:18080 com.cjrequena.sample.grpc.service.AccountService/UpdateAccount
```

## Withdraw account calling account-service via gRPC
```bash
grpcurl --plaintext \
  -d '{"account_id": "b9214d25-8842-4ef0-b5e1-6c1f52cd470f", "amount": "200"}' \
  localhost:18080 com.cjrequena.sample.grpc.service.AccountService/Withdraw
```

## Deposit account calling account-service via gRPC
```bash
grpcurl --plaintext \
  -d '{"account_id": "b9214d25-8842-4ef0-b5e1-6c1f52cd470f", "amount": "200"}' \
  localhost:18080 com.cjrequena.sample.grpc.service.AccountService/Deposit
```

## Create new order calling order-service via gRPC
```bash
grpcurl --plaintext \
  -d '{"order": {"account_id": "4a4b90e5-67fc-4d4d-aa23-316b7b3c6f90", "total": "100.00"}}' \
  localhost:19080 com.cjrequena.sample.grpc.service.OrderService/CreateOrder
```


## Retrieve order by ID calling order-service via gRPC
```bash
grpcurl --plaintext \
  -d '{"id": "37c0c17c-d9c0-4a92-848f-2a9179221400"}' \
  localhost:19080 com.cjrequena.sample.grpc.service.OrderService/RetrieveOrderById
```

## Retrieve orders calling order-service via gRPC
```bash
grpcurl --plaintext \
  localhost:19080 com.cjrequena.sample.grpc.service.OrderService/RetrieveOrders
```
