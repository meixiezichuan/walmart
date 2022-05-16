package com.walmart_6g.gateway.feign;

import entity.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created on 2021/11/2  7:53 下午
 *
 * @author yang hong
 */
@FeignClient(name = "walmart-user-service",url = "${user-service.uri}")
public interface UserService {
    @GetMapping("/api/v1/user/verify")
    ResponseEntity<Response> verify(@RequestParam(value = "uri") String uri,
                                    @RequestParam(value = "token", required = false) String token);

    /**
     * @param token 用户的token
     * @return 该token对应的User
     */
    @RequestMapping("/api/v1/user/id")
    ResponseEntity<Response> getUserByToken(@RequestParam(value = "token") String token);
}
