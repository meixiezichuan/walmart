package com.walmart_6g.walmartGoodsService.service;

import com.walmart_6g.walmartGoodsService.entity.Store;
import entity.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StoreService {
    ResponseEntity<Response> addStore(Store store);
    ResponseEntity<Response> updateStore(String id, Store store);
    ResponseEntity<Response> getStoreByID(String id);
    ResponseEntity<Response> getStoresByIDs(List<String> storeIds);
    ResponseEntity<Response> queryStore(String userId);
}
