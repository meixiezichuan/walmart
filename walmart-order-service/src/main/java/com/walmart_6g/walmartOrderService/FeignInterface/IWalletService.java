package com.walmart_6g.walmartOrderService.FeignInterface;


import entity.Response;
import entity.Wallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "walmart-wallet-service",url = "${wallet-service.uri}")
public interface IWalletService {
    @RequestMapping(value = "/api/v1/wallet/{userId}", method = RequestMethod.PUT)
    ResponseEntity<Response> pay(@PathVariable String userId, Wallet w);

    @RequestMapping(value = "/api/v1/wallet/{userId}", method = RequestMethod.GET)
    ResponseEntity<Response> getBalance(@PathVariable String userId);
}

