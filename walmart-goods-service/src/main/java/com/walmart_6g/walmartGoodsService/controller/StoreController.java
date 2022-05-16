package com.walmart_6g.walmartGoodsService.controller;

import com.walmart_6g.walmartGoodsService.entity.Goods;
import entity.Response;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.walmart_6g.walmartGoodsService.entity.Store;
import com.walmart_6g.walmartGoodsService.service.StoreService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/store")
@Api(value = "Store Service")
public class StoreController {

    @Autowired
    private StoreService storeService;

    // Add store
    @PostMapping("")
    public ResponseEntity<?> addStore(HttpServletRequest request, @RequestBody Store store) {
        return storeService.addStore(store);
    }

    // Update store
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStore(HttpServletRequest request, @PathVariable String id, @RequestBody Store store) {
        return storeService.updateStore(id, store);
    }

    // Get store info by store id
    @GetMapping("/{id}")
    public ResponseEntity<?> getStoreByID(@PathVariable String id) {
        return storeService.getStoreByID(id);
    }

    // Get store info by store ids
    @PostMapping("/ids")
    public ResponseEntity<Response> getStoresByIDs(HttpServletRequest request, @RequestBody HashMap<String, List<String>> storeList){
        return storeService.getStoresByIDs(storeList.get("storeList"));
    }

    // Get store info
    @GetMapping(value = "")
    public ResponseEntity<?> queryStore(@RequestParam("userId") Optional<String> userId) {
        String userid = null;
        if(userId.isPresent()) {
            userid = userId.get();
        }
        ResponseEntity<?> storeList = storeService.queryStore(userid);
        return storeList;
    }
}
