package com.walmart_6g.walmartGoodsService.mapper;

import com.walmart_6g.walmartGoodsService.entity.GoodsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface GoodsCategoryRepository extends JpaRepository<GoodsCategory, String> {

    Optional<GoodsCategory> getGoodsCategoryByNameAndGoodsId(String name, String goodsId);

    ArrayList<GoodsCategory> getGoodsCategoryByGoodsId(String goodsId);

    ArrayList<GoodsCategory> getAllByGoodsId(String goodsId);

    @Query(value="SELECT * FROM goods_category WHERE id=?1", nativeQuery=true)
    GoodsCategory getById(String categoryId);

    @Query(value="SELECT * FROM goods_category WHERE id in ?1", nativeQuery=true)
    ArrayList<GoodsCategory> getByIds(List<String> categoryIds);
}
