package com.cjrequena.sample.service.feign;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.FeignServiceException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 * @version 1.0
 */
//@FeignClient(name = "account-service", url = "${account-service.url}", contextId = "account-service", path = "/account-service/api")
@FeignClient(name = "account-service", contextId = "account-service", path = "/account-service/api")
//@LoadBalancerClient(name = "account-service", configuration = LoadBalancerConfiguration.class)
public interface IAccountServiceFeignClient {

  @GetMapping(
    value = "/accounts/{id}",
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    headers = {"Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1}
  )
  AccountDTO retrieve(@PathVariable(value = "id") UUID id) throws FeignServiceException;

  @PostMapping(
    value = "/accounts/deposit",
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    headers = {"Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1}
  )
  ResponseEntity<Void> deposit(@RequestBody DepositAccountDTO dto) throws FeignServiceException;

  @PostMapping(
    value = "/accounts/withdraw",
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    headers = {"Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1}
  )
  ResponseEntity<Void> withdraw(@RequestBody WithdrawAccountDTO dto) throws FeignServiceException;

}
