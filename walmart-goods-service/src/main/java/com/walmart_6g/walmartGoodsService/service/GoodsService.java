package com.walmart_6g.walmartGoodsService.service;

import com.walmart_6g.walmartGoodsService.entity.GoodsCategory;
import entity.Response;
import com.walmart_6g.walmartGoodsService.entity.Goods;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface GoodsService {
    ResponseEntity<Response> queryGoods(String userId , String storeId, String name, Integer page, Integer pageNum);

    ResponseEntity<Response> addGoods(HttpServletRequest request, Goods goods);

    ResponseEntity<Response> deleteGoods(HttpServletRequest request, String goodsId);

    ResponseEntity<Response> getGoodsByID(String id);

    ResponseEntity<Response> updateGoods(HttpServletRequest request, String id, Goods goods);

    ResponseEntity<Response> addGoodsCategory(HttpServletRequest request, GoodsCategory goodsCategory);

    ResponseEntity<Response> deleteGoodsCategory(HttpServletRequest request, String categoryId);

    ResponseEntity<Response> updateGoodsCategory(HttpServletRequest request, String categoryId, GoodsCategory goodsCategory);

    ResponseEntity<Response> getGoodsCategoryByGoodsId(String goodsId);

    ResponseEntity<Response> getGoodsCategoryById(String categoryId);

    ResponseEntity<Response> getGoodsDetailByCategoryId(String categoryId);

    ResponseEntity<Response> getGoodsDetailByCategoryIds(List<String> categoryIds);
}
