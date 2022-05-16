package com.walmart_6g.walmartGoodsService.feignInterface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import entity.Response;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

@FeignClient(name = "walmart-user-service",url = "${user-service.uri}")
public interface IUserService {
    @RequestMapping("/api/v1/user/hello")
    String hello();

    @RequestMapping("/api/v1/user/verify")
    ResponseEntity<Response> verify(@RequestParam(value = "token") String token,
                                    @RequestParam(value = "uri") String uri);

    @RequestMapping(value = "/api/v1/user/id", method = RequestMethod.GET)
    ResponseEntity<Response> getUserByToken(@RequestParam(value = "token") String token);

    @RequestMapping(method=RequestMethod.POST,value = "/api/v1/user/userBatch")
    ResponseEntity<Response> getUsersByIdBatch(@RequestBody HashMap<String, List<String>> usersequest);

    /*@RequestMapping(method=RequestMethod.POST,value = "/userBatch")
    ResponseEntity<Response> getUsersByIdBatch(@RequestBody HashMap<String, List<String>> users, HttpServletRequest request);*/
    /*@RequestMapping(method = RequestMethod.PUT,value="")
    ResponseEntity<Response> modifyUser();*/
    //需要USER里有一个专门查用户的方法
}
