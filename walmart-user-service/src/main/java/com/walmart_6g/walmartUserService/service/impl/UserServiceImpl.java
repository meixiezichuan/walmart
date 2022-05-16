package com.walmart_6g.walmartUserService.service.impl;

import entity.Response;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart_6g.walmartUserService.entity.RoleAccess;
import com.walmart_6g.walmartUserService.entity.User;
import com.walmart_6g.walmartUserService.mapper.RoleAccessMapper;
import com.walmart_6g.walmartUserService.mapper.UserMapper;
import com.walmart_6g.walmartUserService.service.UserService;
import com.walmart_6g.walmartUserService.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleAccessMapper roleAccessMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public Response insertUser(User user) {
        LOGGER.info("[{}]开始运行","insertUser");
        if (user.getId() != null)
            return new Response<>(400, "不可传入用户id", null);
        if (user.getName().isEmpty() || user.getPwd().isEmpty())
            return new Response<>(400, "用户名或密码为空", null);
        if (user.getRole() < 1 || user.getRole() > 3)
            return new Response<>(400, "身份信息错误", null);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", user.getName());
        User user_query = userMapper.selectOne(wrapper);
        if (user_query != null)
            return new Response<>(409, "用户名冲突", null);
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            LOGGER.error("insert user failed: "  + e.toString());
            e.printStackTrace();
            return new Response<>(500, e.getMessage(), null);
        }
        return new Response<>(200, "SUCCESS", user);
    }

    @Override
    public Response deleteUser(User user) {
        try {
            LOGGER.info("[{}]开始运行","deleteUser");
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            if (user.getId() != null)
                queryWrapper.eq("id", user.getId());
            if (user.getName() != null)
                queryWrapper.eq("name", user.getName());
            int success_num = userMapper.delete(queryWrapper);
            if (success_num == 0)
                return new Response<>(200, "DELETE failed", null);
        } catch (Exception e) {
            LOGGER.error("delete user failed: "  + e.toString());
            return new Response<>(500, e.getMessage(), null);
        }
        return new Response(200, "DELETE USER SUCCESS", null);
    }

    @Override
    public Response viewAllUsers(Integer page_num, Integer page_size) {
        LOGGER.info("[{}]开始运行","viewAllUsers");
        if (page_num <= 0 || page_size <= 0) {
            return new Response(400, "OUT OF RANGE", null);
        }
////////
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        Map<String, Object> map = new HashMap<>();
        Page<User> page = new Page<>(page_num, page_size);
        IPage<Map<String, Object>> iPage = userMapper.selectMapsPage(page, query);
        System.out.println("总页数：" + page.getPages());
        System.out.println("总记录数：" + iPage.getTotal());
        List<Map<String, Object>> list = iPage.getRecords();
        map.put("user_list", list);
        map.put("total_num", iPage.getTotal());
        list.forEach(System.out::println);
        ////////////
//        IPage<User> mapIPage =selectPage(new Page<>(page_num,page_size));
//        System.out.println("总页数： "+mapIPage.getPages());
//        System.out.println("总记录数： "+mapIPage.getTotal());
//        mapIPage.getRecords().forEach(System.out::println);
        return new Response(200, "SUCCESS", map);
    }

    @Override
    public Response getUserById(String id, String token) {
        LOGGER.info("[{}]开始运行","getUserById");
//        Response response=getUserIDByToken(token);
//        String userId=response.getData().toString();
        User user = userMapper.selectById(id);
//        User user=userMapper.selectById(UUID.fromString(userId));
        if (user == null) {
            return new Response<>(400, "No user", null);
        } else {
            user.setPwd(null);
            return new Response<>(200, "SUCCESS", user);
        }
    }


    @Override
    public ResponseEntity<Response> getUsersByIds(List<String> userIds)
    {
        LOGGER.info("[{}]开始运行","getUsersByIds");
        if(userIds.size()!=0) {
            List<User> users = userMapper.selectBatchIds(userIds);
            for (int i = 0; i < users.size(); i++)
                users.get(i).setPwd(null);
            if (users.size() == 0) {
                return ResponseEntity.status(200).body(new Response<>(400, "No Users match", null));
            } else {
                return ResponseEntity.status(200).body(new Response<>(200, "SUCCESS", users));
            }
        }else{
            return ResponseEntity.status(200).body(new Response<>(400, "no userIds", null));
        }
    }

    @Override
    public IPage<User> selectPage(Page<User> page) {
        LOGGER.info("[{}]开始运行","selectPage");
        return userMapper.selectPage(page);
    }

    @Override
    public Response getAdmin() {
        LOGGER.info("[{}]开始运行","getAdmin");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role",User.admin_role);
        List<User> users = userMapper.selectList(queryWrapper);
        User reUser =null;
        for(User user: users){
            if(!user.getId().equals("")){
                reUser = user;
                break;
            }
        }
        reUser.setPwd(null);
        return new Response(200, "查询成功", reUser);
    }

    @Override
    public Response modifyUser(User user,HttpServletRequest request) {
        LOGGER.info("[{}]开始运行","modifyUser");
        if (user.getId() == null)
            return new Response<>(400, "id is null", null);
        String userId=user.getId();
//        UUID userId=UUID.fromString(user.getId());
        String token = request.getHeader("token");
        String userIdFromToken= TokenUtil.getUserID(token);
        User userModify = userMapper.selectById(userIdFromToken);
        System.out.println(userModify.getRole());
        if(userModify.getRole()==1){
            userMapper.updateById(user);
            return new Response<>(200, "Modify success", null);
        }
//        UUID userIdFromToken=UUID.fromString(response.getData().toString());
        if (userId.equals(userIdFromToken)) {
            userMapper.updateById(user);
            return new Response<>(200, "Modify success", null);
        } else {
            return new Response<>(403, "Modify fail", null);
        }
    }

    @Override
    public Response login(String username,String password) {
        LOGGER.info("[{}]开始运行","login");
        if (username.equals("")){
            return new Response<>(400, "Username is empty", null);
        }else if (password.equals("")){
            return new Response<>(400, "Password is empty", null);
        }else {
            Map<String,Object> map = new HashMap<>();
            map.put("name",username);
            List<User> userlist =  userMapper.selectByMap(map);
            if (userlist.size()==0){
                return new Response<>(400, "The user does not exist", null);
            }else {
                User user = userlist.get(0);
                if (!user.getPwd().equals(password)) {
                    return new Response<>(403, "Username or password incorrect", null);
                } else {
                    user.setPwd(null);
                    return new Response<>(200, "login success", user);
                }
            }
        }
    }

    @Override
    public Response getUserByToken(String token) {
        try{
            LOGGER.info("[{}]开始运行","getUserByToken");
            String userID = TokenUtil.getUserID(token);
            User user = userMapper.selectById(userID);
            user.setPwd(null);
            return new Response<>(200, "select success", user);
        }catch (Exception e){
            LOGGER.error("get user by token failed: "  + e.toString());
            return new Response<>(400,"token is error","null");
        }
    }

    @Override
    public Response verify(String token, String uri) {
        LOGGER.info("[{}]开始运行","verify");
        if(token==null) {
            Map<String, Object> map = new HashMap<>();
            map.put("url", uri);
            List<RoleAccess> roleAccess = roleAccessMapper.selectByMap(map);
            for (RoleAccess i:roleAccess){
                if (i.getRole_id() == 0){
                    return new Response<>(200,"have permissions",null);
                }
            }
            return new Response<>(401,"token is expire,please log in again",null);
        }
        if(TokenUtil.verify(token)){
            String userid = TokenUtil.getUserID(token);
            User user = userMapper.selectById(userid);
            if(user==null){
                return new Response<>(401,"token is expire,please log in again",null);
            }
            user.setAddress(null);
            user.setEmail(null);
            user.setPhone_num(null);
            user.setPwd(null);
            user.setName(null);
            Map<String, Object> map = new HashMap<>();
            map.put("url", uri);
            List<RoleAccess> roleAccess = roleAccessMapper.selectByMap(map);
            for (RoleAccess i:roleAccess){
                if (i.getRole_id() == 0 || i.getRole_id() == user.getRole()){
                    return new Response<>(200,"have permissions",user);
                }
            }
            return new Response<>(401,"not authoried",user);
        }else{
            return new Response<>(401,"token is expire,please log in again",null);
        }
    }


}
