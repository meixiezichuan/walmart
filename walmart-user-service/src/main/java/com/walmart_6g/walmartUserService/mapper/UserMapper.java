package com.walmart_6g.walmartUserService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart_6g.walmartUserService.entity.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
    /**
     * delete by user id
     *
     * @param userId user id
     * @return null
     */
//    void deleteByUserId(UUID userId);

    /**
     * find by username
     *
     * @param username username
     * @return Optional<User>
     */
//    Optional<User> findByUsername(String username);

//    User findById(UUID userId);

//    ArrayList<User> findAll();

    IPage<User> selectPage(Page page);
}
