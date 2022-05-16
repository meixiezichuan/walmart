package com.walmart_6g.walmartGoodsService.mapper;

import com.walmart_6g.walmartGoodsService.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public interface StoreMapper extends JpaRepository<Store, Long> {

    Store findById(String id);

    @Query(value="SELECT * FROM store WHERE name=?1 AND user_id=?2", nativeQuery=true)
    Store findByNameAndUserId(String name, String user_id);

    @Query(value="SELECT * FROM store WHERE user_id=?1", nativeQuery=true)
    ArrayList<Store> findByUserId(String userId);

    @Query(value="SELECT * FROM store WHERE id in ?1", nativeQuery=true)
    ArrayList<Store> findByIds(List<String> id);

}
