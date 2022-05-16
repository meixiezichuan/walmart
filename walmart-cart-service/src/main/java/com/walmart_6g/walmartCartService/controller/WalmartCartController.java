package com.walmart_6g.walmartCartService.controller;

import com.walmart_6g.walmartCartService.entity.CartItem;
import com.walmart_6g.walmartCartService.service.WalmartCartService;
import entity.Response;
import entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cart")
@Api(value = "Cart Service")
public class WalmartCartController {

//    @Autowired
//    private DiscoveryClient discoveryClient;

    @Autowired
    private WalmartCartService cartService;

    Boolean CheckUser(HttpServletRequest request, String user_id) {
        String userId = request.getHeader("userId");
        String role = request.getHeader("role");
        if(userId == null || role == null) {
            return false;
        }
        Integer roleValue = Integer.parseInt(role);
        return (roleValue == User.customer_role && Objects.equals(userId, user_id));
    }

    //query goods which in cart
    @ApiOperation("query goods which in cart")
    @GetMapping("/{user_id}")
    public ResponseEntity<?> queryItemsInCart(HttpServletRequest request, @PathVariable String user_id, @RequestParam("pageNo") Optional<Integer> page,
                                              @RequestParam("pageSize") Optional<Integer> pageNum) {
        if(!CheckUser(request, user_id)){
            return ResponseEntity.status(403).body(new Response(403, "Forbiden", null));
        }
        Integer pageNo = 1;
        Integer pageValue = 10;

        if(page.isPresent())
        {
            pageNo = page.get();
        }
        if(pageNum.isPresent()) {
            pageValue = pageNum.get();
        }
        return  cartService.queryGoodsInCart(user_id, pageValue, pageNo);
    }

    // Add goods to cart
    @ApiOperation("add goods to cart")
    @PostMapping("/{user_id}")
    public ResponseEntity<?> addGoodsToCart(HttpServletRequest request, @PathVariable String user_id, @RequestBody CartItem cartGoods) {
        if(!cartGoods.CheckValid()){
            return ResponseEntity.status(400).body(new Response<>(400, "Bad request body", null));
        }
        if(!CheckUser(request, user_id)){
            return ResponseEntity.status(403).body(new Response(403, "Forbiden", null));
        }
        return cartService.addGoodsToCart(user_id, cartGoods);
    }

    // Update cart
    @ApiOperation("update goods in cart")
    @PutMapping("/{user_id}")
    public ResponseEntity<?> updateGoodsInCart(HttpServletRequest request, @PathVariable String user_id, @RequestBody CartItem cartGoods) {
        if(!CheckUser(request, user_id)){
            return ResponseEntity.status(403).body(new Response(403, "Forbiden", null));
        }
        return cartService.updateGoodsInCart(user_id, cartGoods);
    }

    // Delete goods from cart
    @ApiOperation("delete goods from cart")
    @PostMapping("/delete/{user_id}")
    public ResponseEntity<?> deleteGoodsFromCart(HttpServletRequest request, @PathVariable String user_id, @RequestBody HashMap<String, List<String>> categoryList) {
        if(!CheckUser(request, user_id)){
            return ResponseEntity.status(403).body(new Response(403, "Forbiden", null));
        }
        return cartService.deleteGoodsFromCart(user_id, categoryList.get("categoryList"));
    }

}
