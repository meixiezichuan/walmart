package com.walmart_6g.walmartGoodsService.service.impl;

import com.walmart_6g.walmartGoodsService.entity.Store;
import com.walmart_6g.walmartGoodsService.mapper.StoreMapper;
import com.walmart_6g.walmartGoodsService.service.StoreService;
import entity.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    private StoreMapper storeMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreServiceImpl.class);

    @Override
    public ResponseEntity<Response> addStore(Store store) {
        LOGGER.info("[{}]开始运行","addStore");
        Store s = storeMapper.findByNameAndUserId(store.getName(), store.getUserId());
        if(s != null){
            return ResponseEntity.status(409).body(new entity.Response<>(409, "Store already exist", null));
        }
        try {
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            String create_time = formatter.format(date);

            store.setCreateTime(create_time);
            store.setGrade(1);
            store.setStatus("Open");

            storeMapper.save(store);
            return ResponseEntity.ok(new entity.Response<>(200, "SUCCESS", store));
        } catch (Exception e) {
            LOGGER.error("add store failed: "  + e.toString());
            return ResponseEntity.status(400).body(new entity.Response<>(400, "Bad request", null));
        }
    }

    @Override
    public ResponseEntity<Response> updateStore(String id, Store store) {
        LOGGER.info("[{}]开始运行","updateStore");
        Store oldInfo = storeMapper.findById(id);
        if(oldInfo == null){
            return ResponseEntity.status(404).body(new Response<>(404, "Cannot find the store", null));
        }
        try {
            boolean updated=false;
            if(store.getDescription() != null) {
                oldInfo.setDescription(store.getDescription());
                updated=true;
            }
            if(store.getStatus() != null) {
                oldInfo.setStatus(store.getStatus());
                updated=true;
            }
            if(updated) {
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                Date date = new Date(System.currentTimeMillis());
                String create_time = formatter.format(date);
                oldInfo.setCreateTime(create_time);
                storeMapper.save(oldInfo);
            }
            return ResponseEntity.ok(new Response<>(200, "SUCCESS", oldInfo));
        } catch (Exception e) {
            LOGGER.error("update store failed: "  + e.toString());
            return ResponseEntity.status(400).body(new Response<>(400, "Bad request", null));
        }
    }

    @Override
    public ResponseEntity<Response> getStoreByID(String id) {
        LOGGER.info("[{}]开始运行","getStoreByID");
        Store store = storeMapper.findById(id);
        if(store == null) {
            return ResponseEntity.status(404).body(new Response<>(404, "No store", null));
        } else {
            return ResponseEntity.ok(new Response<>(200, "Get store info success", store));
        }
    }

    @Override
    public ResponseEntity<Response> getStoresByIDs(List<String> storeIds) {
        try {
            List<Store> stores = storeMapper.findByIds(storeIds);
            return ResponseEntity.ok(new Response<>(200, "Get store info success", stores));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new Response(404, "No store", null));
        }
    }

    @Override
    public ResponseEntity<Response> queryStore(String userId) {
        LOGGER.info("[{}]开始运行","queryStore");
        ArrayList<Store> storeList;
        storeList = storeMapper.findByUserId(userId);

        Map data = new HashMap();
        data.put("stores", storeList);

        return ResponseEntity.ok(new Response<>(200, "SUCCESS", data));
    }
}
