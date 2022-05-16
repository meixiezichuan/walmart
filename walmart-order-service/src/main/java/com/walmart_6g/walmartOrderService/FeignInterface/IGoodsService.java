package com.walmart_6g.walmartOrderService.FeignInterface;

import entity.GoodsCategory;
import entity.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping(value = "/api/v1/store")
    public ResponseEntity<Response> queryStore(@RequestParam("userId") String userId);

    @GetMapping(value = "/api/v1/store/{id}")
    public ResponseEntity<Response> queryStoreByStoreId(@PathVariable String id);

    @PostMapping("/api/v1/store/ids")
    public ResponseEntity<Response> getStoresByIDs(@RequestBody HashMap<String, List<String>> storeList);
}
