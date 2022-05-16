package com.walmart_6g.walmartCartService.service;

import com.walmart_6g.walmartCartService.entity.CartItem;
import entity.Response;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface WalmartCartService {
    //ResponseEntity<Response> queryGoodsInCart(String user_id,Integer pageNo,Integer pageSize);

    ResponseEntity<Response> addGoodsToCart(String user_id, CartItem cartIterm);

    ResponseEntity<Response> updateGoodsInCart(String user_id, CartItem cartIterm);

    ResponseEntity<Response> deleteGoodsFromCart(String user_id, List<String>  cartGoodsList);

    ResponseEntity<Response> queryGoodsInCart(String user_id, Integer pageSize, Integer pageNo);

    ArrayList<CartItem> getGoodsInCart(String user_id, Integer pageNo, Integer pageSize);
}
