package com.walmart_6g.walmartCartService.repository;

import com.walmart_6g.walmartCartService.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface WalmartCartRepository extends JpaRepository<CartItem, Long> {

    @Query(value="SELECT * FROM cart_item where user_id=?1 LIMIT ?2,?3", nativeQuery = true)
    ArrayList<CartItem> findCartGoodsByUserIdAndPage(String user_id,Integer lowPageNum,Integer highPageNum);

    @Query(value="SELECT COUNT(*) FROM cart_item where user_id=?1",nativeQuery = true)
    Integer countDifferentItemByUserId(String user_id);   //返回该用户加购的不同商品的数量

    @Query(value="SELECT sum(num) from cart_item where user_id=?1", nativeQuery = true)
    Integer countByUserId(String user_id);

    @Query(value="SELECT * from cart_item where user_id=?1 and category_id=?2", nativeQuery = true)
    CartItem findByUserIdAndCategoryId(String user_id, String category_id);

    @Modifying
    @Query(value="DELETE from cart_item where user_id=?1 and category_id in ?2", nativeQuery = true)
    void deleteByUserIdAndCategoryId(String user_id, List<String> category_id);

}
