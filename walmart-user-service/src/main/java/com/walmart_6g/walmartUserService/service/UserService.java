package com.walmart_6g.walmartUserService.service;

import entity.Response;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart_6g.walmartUserService.entity.User;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    Response insertUser(User user);

    Response deleteUser(User user);

    Response viewAllUsers(Integer page_num,Integer page_size);

    Response modifyUser(User user,HttpServletRequest request);

    Response login(String username,String password);

    Response getUserByToken(String token);

    Response getUserById(String id,String token);

    Response verify(String token,String uri);

    ResponseEntity<Response> getUsersByIds(List<String> id);

    IPage<User> selectPage(Page<User> page);

    Response getAdmin();
}
