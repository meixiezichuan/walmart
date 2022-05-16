package com.walmart_6g.walmartCartService.feignInterface;

import entity.GoodsCategory;
import entity.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;

@FeignClient(name = "walmart-goods-service",url = "${goods-service.uri}")
public interface IGoodsService {
    @RequestMapping(value = "/api/v1/goods/{id}", method = RequestMethod.GET)
    ResponseEntity<Response> getGoodsByID(@PathVariable String id);

    @RequestMapping(value = "/api/v1/goods/category/{id}", method = RequestMethod.GET)
    ResponseEntity<Response> getGoodCateByID(@PathVariable String id);

    @RequestMapping(value = "/api/v1/goods/category/{id}", method = RequestMethod.PUT)
    ResponseEntity<Response> UpdateGC(@PathVariable String id, GoodsCategory gd);

    @RequestMapping(value = "/api/v1/goods/categories", method = RequestMethod.POST)
    ResponseEntity<Response> getGoodsDetailsByCategoryIds(@RequestBody HashMap<String, List<String>> categoryList);
}
