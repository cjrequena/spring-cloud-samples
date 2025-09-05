# spring-cloud-grpc-sample

## Create new account calling account-service via gRPC
```bash
grpcurl --plaintext \
  -d '{"account": {"owner": "Alice", "balance": "1000.00"}}' \
  localhost:18080 com.cjrequena.sample.service.AccountService/CreateAccount
```


## Retrieve account by ID calling account-service via gRPC
```bash
grpcurl --plaintext \
  -d '{"id": "37c0c17c-d9c0-4a92-848f-2a9179221400"}' \
  localhost:18080 com.cjrequena.sample.service.AccountService/RetrieveAccountById
```
