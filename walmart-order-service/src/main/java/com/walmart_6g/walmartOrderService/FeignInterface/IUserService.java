package com.walmart_6g.walmartOrderService.FeignInterface;

import entity.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "walmart-user-service",url = "${user-service.uri}")
public interface IUserService {
    @RequestMapping(value = "/api/v1/user/id", method = RequestMethod.GET)
    ResponseEntity<Response> getUserByToken(@RequestParam(value = "token") String token);

    @RequestMapping(value = "/api/v1/user/admin", method = RequestMethod.GET)
    ResponseEntity<Response> getAdmin();

}
