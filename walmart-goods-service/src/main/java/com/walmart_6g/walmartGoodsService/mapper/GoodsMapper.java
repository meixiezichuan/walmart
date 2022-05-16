package com.walmart_6g.walmartGoodsService.mapper;

import com.walmart_6g.walmartGoodsService.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface GoodsMapper extends JpaRepository<Goods, Long> {
    /**
     * delete by id
     *
     * @param id goods id
     * @return null
     */
    void deleteById(String id);

    /**
     * find by name
     *
     * @param name goods name
     * @return Optional<Goods>
     */
    ArrayList<Goods> findByNameContainingAndDeprecatedNot(String name, Boolean deprecated);

    ArrayList<Goods> findByDeprecatedNot(Boolean deprecated);

    Goods findById(String id);

    @Query(value="SELECT * FROM goods WHERE id in ?1", nativeQuery=true)
    ArrayList<Goods> findByIds(List<String> ids);

    //条件查询
    @Query(value="SELECT * FROM goods WHERE deprecated=0 AND name LIKE %?1% and IF(?2 is null, 0=0, user_id=?2) and IF(?3 is null, 0=0, store_id=?3) LIMIT ?4,?5", nativeQuery=true)
    public ArrayList<Goods> findByName(String name, String userId, String storeId, Integer page, Integer pageNum);

    @Query(value="SELECT count(*) from goods where deprecated=0 and name like %?1% and IF(?2 is null, 0=0, user_id=?2) and IF(?3 is null, 0=0, store_id=?3)", nativeQuery = true)
    Integer findByNameNum(String name, String userId, String storeId);

    @Query(value="SELECT user_id FROM goods WHERE deprecated=0", nativeQuery=true)
    public ArrayList<String> findAllUsersIds();

}
