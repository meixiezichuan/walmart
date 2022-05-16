package com.walmart_6g.walmartCartService.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart_6g.walmartCartService.entity.CartItem;
import com.walmart_6g.walmartCartService.entity.CartItemDetail;
import com.walmart_6g.walmartCartService.feignInterface.IGoodsService;
import com.walmart_6g.walmartCartService.repository.WalmartCartRepository;
import com.walmart_6g.walmartCartService.service.WalmartCartService;
import entity.GoodsCategory;
import entity.GoodsDetail;
import entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CartServiceImpl implements WalmartCartService {

    @Autowired
    private WalmartCartRepository cartRepository;

    @Autowired
    private IGoodsService iGoodsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CartServiceImpl.class);

    GoodsCategory getGCByCategoryId(String categoryId) {
        LOGGER.info("[{}]开始运行","getGCByCategoryId");
        GoodsCategory goods = null;
        ResponseEntity<Response> response = iGoodsService.getGoodCateByID(categoryId);
        if (response.getStatusCodeValue() == 200 ){
            Object o = response.getBody().getData();
            ObjectMapper objectMapper = new ObjectMapper();
            goods = objectMapper.convertValue(o, GoodsCategory.class);
        }
        return goods;
    }

    @Override
    public ArrayList<CartItem> getGoodsInCart(String user_id,Integer pageNo,Integer pageSize) {
        LOGGER.info("[{}]开始运行","getGoodsInCart");
        Integer lowPageNum = (pageNo-1) * pageSize;
        Integer highPageNum = pageNo * pageSize;
        System.out.println(pageNo+" "+pageSize+" "+user_id);
        ArrayList<CartItem> cartItemList = cartRepository.findCartGoodsByUserIdAndPage(user_id,lowPageNum,highPageNum);
        System.out.println("cartitemlist="+cartItemList);
        return cartItemList;
    }

    @Override
    public ResponseEntity<Response> queryGoodsInCart(String user_id, Integer pageSize, Integer pageNo)
    {
        LOGGER.info("[{}]开始运行","queryGoodsInCart");
        Integer lowPageNum = (pageNo-1) * pageSize;
        Integer highPageNum = pageNo * pageSize;
        System.out.println(pageNo+" "+pageSize);
        ArrayList<CartItem> cartItemList = cartRepository.findCartGoodsByUserIdAndPage(user_id,lowPageNum,highPageNum);
        ArrayList<String> cids = new ArrayList<String>();
        for(CartItem ci: cartItemList){
            cids.add(ci.getCategoryId());
        }
        if(cids.isEmpty()){
            Map data = new HashMap();
            data.put("cart", new ArrayList());
            data.put("totalPage",0);
            data.put("totalNumOfItem",0);
            return ResponseEntity.status(200).body(new Response<>(200,"Nothing found", data));
        }

        HashMap<String, List<String>> reqBody = new HashMap<>();
        reqBody.put("categoryList", cids);
        ResponseEntity<Response> response =iGoodsService.getGoodsDetailsByCategoryIds(reqBody);
        if (response.getStatusCodeValue() != 200 ){
            return ResponseEntity.status(response.getStatusCodeValue()).body(response.getBody());
        }
        List<CartItemDetail> cds = new ArrayList<>();
        try {
            Object o = response.getBody().getData();
            List<Object> gds = (List<Object>) o;
            HashMap<String, GoodsDetail> gdmp = new HashMap();
            ObjectMapper objectMapper = new ObjectMapper();
            for (Object og : gds) {
                GoodsDetail gd = objectMapper.convertValue(og, GoodsDetail.class);
                gdmp.put(gd.getCategoryId(), gd);
            }
            for(CartItem ci: cartItemList){
                cds.add(new CartItemDetail(gdmp.get(ci.getCategoryId()), ci.getNum(), ci.getUpdateTime()));
            }
            Integer totalNumOfDifferentItem = cartRepository.countDifferentItemByUserId(user_id);
            Integer totalPage = (int) Math.ceil(totalNumOfDifferentItem.doubleValue() / pageSize) ;
            Map data = new HashMap();
            data.put("cart",cds);
            data.put("totalPage",totalPage);
            data.put("totalNumOfItem",totalNumOfDifferentItem);
            return ResponseEntity.status(200).body(new Response<>(200,"SUCCESS",data));
        }catch (Exception e){
            LOGGER.error("query goods in cart failed: "  + e.toString());
            return ResponseEntity.status(500).body(new Response<>(500,"Network error",null));
        }
    }

    @Override
    public ResponseEntity<Response> addGoodsToCart(String user_id, CartItem cartItem) {
        LOGGER.info("[{}]开始运行","addGoodsToCart");
        CartItem oldInfo = cartRepository.findByUserIdAndCategoryId(user_id, cartItem.getCategoryId());
        if(oldInfo==null){
            try {
                CartItem newCart = new CartItem(user_id, cartItem.getCategoryId(), cartItem.getNum());
                cartRepository.save(newCart);
                Integer totalNum = cartRepository.countByUserId(user_id);
                Map<String, Object> data = new HashMap<String, Object>() {{
                    put("totalNumOfGoods", totalNum);
                }};
                return ResponseEntity.ok(new Response<>(200, "SUCCESS", data));
            } catch (Exception e) {
                LOGGER.error("add goods to cart failed: "  + e.toString());
                return ResponseEntity.status(400).body(new Response<>(400, "Can not add", null));
            }
        } else {
            GoodsCategory goods = getGCByCategoryId(oldInfo.getCategoryId());
            Integer stock = goods.getNum();
            Integer updatedNum = cartItem.getNum() + oldInfo.getNum();
            if( updatedNum > stock){
                updatedNum = stock;
            }
            oldInfo.setNum(updatedNum);
            oldInfo.UpdateTime();
            cartRepository.save(oldInfo);
            Integer totalNum = cartRepository.countByUserId(user_id);
            Map<String, Object> data = new HashMap<String, Object>() {{
                put("totalNumOfGoods", totalNum);
            }};
            return ResponseEntity.ok(new Response<>(200, "SUCCESS", data));
        }
    }

    @Override
    public ResponseEntity<Response> updateGoodsInCart(String user_id, CartItem cartItem) {
        LOGGER.info("[{}]开始运行","updateGoodsInCart");
        CartItem oldInfo = cartRepository.findByUserIdAndCategoryId(user_id, cartItem.getCategoryId());

        if(oldInfo==null){
            Map<String, Object> data = new HashMap<String, Object>() {{
                put("categoryId", cartItem.getCategoryId());
                put("num", 0);
            }};
            return ResponseEntity.status(404).body(new Response<>(404, "Cannot find the product", data));
        } else {
            GoodsCategory goods = getGCByCategoryId(oldInfo.getCategoryId());
            Integer stock = goods.getNum();
            if( cartItem.getNum() > stock){
                oldInfo.setNum(cartItem.getNum());
                oldInfo.UpdateTime();
                cartRepository.save(oldInfo);
                Map<String, Object> data = new HashMap<String, Object>() {{
                    put("categoryId", oldInfo.getCategoryId());
                    put("num", oldInfo.getNum());
                }};
                return ResponseEntity.status(409).body(new Response<>(409, "Cannot exceed the inventory of the product", data));
            }
            oldInfo.setNum(cartItem.getNum());
            oldInfo.UpdateTime();
            cartRepository.save(oldInfo);
            Map<String, Object> data = new HashMap<String, Object>() {{
                put("categoryId", oldInfo.getCategoryId());
                put("num", oldInfo.getNum());
            }};
            return ResponseEntity.ok(new Response<>(200, "SUCCESS", data));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Response> deleteGoodsFromCart(String user_id, List<String> cartGoodsList) {
        LOGGER.info("[{}]开始运行","deleteGoodsFromCart");
        if(cartGoodsList.size() == 0) {
            return ResponseEntity.status(400).body(new Response<>(400, "Must specify goods_list", null));
        }
        try {
            cartRepository.deleteByUserIdAndCategoryId(user_id, cartGoodsList);
            return ResponseEntity.ok(new Response<>(200, "SUCCESS", null));
        } catch (Exception e) {
            LOGGER.error("delete goods from cart failed: "  + e.toString());
            return ResponseEntity.status(409).body(new Response<>(409, "Can not delete", null));
        }
    }

}
