package com.walmart_6g.walmartGoodsService.controller;

import com.walmart_6g.walmartGoodsService.entity.Goods;
import com.walmart_6g.walmartGoodsService.entity.GoodsCategory;
import com.walmart_6g.walmartGoodsService.feignInterface.IUserService;
import com.walmart_6g.walmartGoodsService.service.GoodsService;
import entity.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/goods")
@Api(value = "Goods Service")
public class GoodsController {
//    Accustomed to get ip and port of instances
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/hello")
    public String hello(){
        return "Welcome to Goods Service!";
    }

//  Method 1: Use Restful Controller
    @ApiOperation("say hello to user")
    @GetMapping("/sayHelloToUser")
    public String helloToUser(){
        String s = restTemplate.getForObject("http://walmart-user-service/user/hello", String.class);
        return "copied that!\n" + s;
    }

//  Method 2: Use OpenFeign (recommended)
    @ApiOperation("say bye to user")
    @GetMapping("/sayByeToUser")
    public String byeToUser(){
        String s = iUserService.hello();
        return "Bye!\n" + s;
    }

    // Add goods
    @PostMapping("")
    public ResponseEntity<?> addGoods(HttpServletRequest request, @RequestBody Goods good) {
        return goodsService.addGoods(request, good);
    }

    // Update goods
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoods(HttpServletRequest request, @PathVariable String id, @RequestBody Goods good) {
        return goodsService.updateGoods(request, id, good);
    }

    // Delete goods
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoods(HttpServletRequest request, @PathVariable String id) {
        return goodsService.deleteGoods(request, id);
    }

    // Get goods by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getGoodsByID(@PathVariable String id) {
        return goodsService.getGoodsByID(id);
    }

    @ApiOperation("get all goods if no key else specific goods")
    @ResponseBody
    @GetMapping(value="")
    public ResponseEntity<?> queryGoods(@RequestParam("name") Optional<String> name,
                                  @RequestParam("page") Optional<Integer> page,
                                  @RequestParam("pageNum") Optional<Integer> pageNum,
                                        @RequestParam("userId") Optional<String> userId,
                                        @RequestParam("storeId") Optional<String> storeId)
    {
        String goodsName = "";
        Integer pageNumValue = 10;
        Integer pageValue = 1;
        String userid = null;
        String storeid = null;
        System.out.println(pageNumValue);
        if(name.isPresent()&&!name.equals(Optional.of("")))
        {
            goodsName = name.get();
        }
        if(page.isPresent())
        {
            pageValue = page.get();
        }
        if(pageNum.isPresent())
        {
            pageNumValue = pageNum.get();
        }
        if(userId.isPresent())
        {
            userid = userId.get();
        }
        if(storeId.isPresent())
        {
            storeid = storeId.get();
        }
        ResponseEntity<?> goodsList = goodsService.queryGoods(userid, storeid, goodsName,pageValue,pageNumValue);
        return goodsList;
    }

    @PostMapping("/category")
    public ResponseEntity<Response> addGoodsCategory(HttpServletRequest request, @RequestBody GoodsCategory goodsCategory){
        return goodsService.addGoodsCategory(request, goodsCategory);
    }

    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<Response>deleteGoodsCategory(HttpServletRequest request, @PathVariable String categoryId){
        return goodsService.deleteGoodsCategory(request, categoryId);
    }

    @PutMapping("/category/{categoryId}")
    public ResponseEntity<Response> updateGoodsCategory(HttpServletRequest request, @PathVariable String categoryId, @RequestBody GoodsCategory goodsCategory){
        return goodsService.updateGoodsCategory(request, categoryId, goodsCategory);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Response> getGoodsCategoryById(HttpServletRequest request, @PathVariable String categoryId){
        return goodsService.getGoodsCategoryById(categoryId);
    }

    @GetMapping("/category/detail/{categoryId}")
    public ResponseEntity<Response> getGoodsCategoryDetailById(HttpServletRequest request, @PathVariable String categoryId){
        return goodsService.getGoodsDetailByCategoryId(categoryId);
    }

    @PostMapping("/categories")
    public ResponseEntity<Response> getGoodsCategoryDetailByIds(HttpServletRequest request, @RequestBody HashMap<String, List<String>> categoryList){
        return goodsService.getGoodsDetailByCategoryIds(categoryList.get("categoryList"));
    }

    @GetMapping("/category")
    public ResponseEntity<Response> getGoodsCategoryByGoodsId(@RequestParam("goodsId") String goodsId){
        return goodsService.getGoodsCategoryByGoodsId(goodsId);
    }
}

