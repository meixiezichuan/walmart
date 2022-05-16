package com.walmart_6g.walmartUserService.controller;

import com.walmart_6g.walmartUserService.entity.User;
import com.walmart_6g.walmartUserService.service.UserService;
import com.walmart_6g.walmartUserService.util.TokenUtil;
import entity.Response;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@Api(value = "User Controller", tags = "用户相关操作接口")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> userInsert(@RequestBody User user){
        Response response = userService.insertUser(user);
        MultiValueMap<String, String> headers = new HttpHeaders();
        return new ResponseEntity(response,headers,response.getStatus());
    }



    /**
     * 根据条件删除user，现在只支持用id删除
     */

    @DeleteMapping()
    public ResponseEntity<Response> deleteUser(@RequestBody User user) {
        if (user == null || user.getId()==null || user.getPwd()!=null
                || user.getName()!=null || user.getAddress()!=null
                || user.getEmail()!=null || user.getPhone_num()!=null
                || user.getRole()!=0){
            return new ResponseEntity(new Response<>(400,"Bad Request", null),new HttpHeaders(),400);
        }
        Response response = userService.deleteUser(user);
        MultiValueMap<String, String> headers = new HttpHeaders();
        return new ResponseEntity(response,headers,response.getStatus());
    }


    @GetMapping
    public ResponseEntity<Response> viewAllUsers(@RequestParam(value="page_num") Integer page_num,
                                                 @RequestParam(value="page_size") Integer page_size) {
        Response response=userService.viewAllUsers(page_num,page_size);
        MultiValueMap<String, String> headers = new HttpHeaders();
        return new ResponseEntity(response,headers,response.getStatus());
    }


    @GetMapping(path = "/{userId}")
    public ResponseEntity<Response> getUserById(@PathVariable String userId,
                                                HttpServletRequest request){
        String token = request.getHeader("token");
        Response response=userService.getUserById(userId,token);
        MultiValueMap<String, String> headers = new HttpHeaders();
        return new ResponseEntity(response,headers,response.getStatus());
    }


    @PostMapping(path = "/userBatch")
    public ResponseEntity<Response> getUsersByIdBatch(@RequestBody HashMap<String, List<String>> users, HttpServletRequest request)
    {
        List<String> userIds = users.get("userIds");
        return userService.getUsersByIds(userIds);
    }


    @PutMapping
    public ResponseEntity<Response> modifyUser(@RequestBody User user, HttpServletRequest request){
        Response response=userService.modifyUser(user,request);
        MultiValueMap<String, String> headers = new HttpHeaders();
        return new ResponseEntity(response,headers,response.getStatus());
    }

    @GetMapping("/id")
    public ResponseEntity<Response> getUserByToken(@RequestParam(value = "token") String token){
        MultiValueMap<String, String> headers = new HttpHeaders();
        return new ResponseEntity(userService.getUserByToken(token),headers,userService.getUserByToken(token).getStatus());
    }

    @GetMapping("/verify")
    public ResponseEntity<Response> verify(@RequestParam(value = "uri") String uri,
                                           @RequestParam(value = "token", required = false) String token){
        MultiValueMap<String, String> headers = new HttpHeaders();
        return new ResponseEntity(userService.verify(token,uri).getData(),headers,userService.verify(token, uri).getStatus());
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody User user_
    ){
        Response<User> response = userService.login(user_.getName(),user_.getPwd());
        System.out.println(user_.getName());
        User user = response.getData();
        if(user!=null) {
            String token = TokenUtil.createToken(user.getId());
            MultiValueMap<String, String> headers = new HttpHeaders();
            headers.add("token",token);
            return new ResponseEntity(response,headers,response.getStatus());
        }else {
            MultiValueMap<String, String> headers = new HttpHeaders();
            return new ResponseEntity(response,headers,response.getStatus());
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<Response> getAdminId(){
        MultiValueMap<String, String> headers = new HttpHeaders();
        Response<String> response = userService.getAdmin();
        return new ResponseEntity<>(response, headers, response.getStatus());
    }
}
