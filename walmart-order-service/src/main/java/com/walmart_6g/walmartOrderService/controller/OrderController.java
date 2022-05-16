package com.walmart_6g.walmartOrderService.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.walmart_6g.walmartOrderService.entity.*;
import com.walmart_6g.walmartOrderService.service.OrderService;
import com.walmart_6g.walmartOrderService.vo.OrderVO;
import entity.Response;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanghong
 */
@RestController
@RequestMapping("api/v1/order")
@Api(value = "Order", tags = "订单相关接口")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @GetMapping("/subOrder/list")
    @ApiOperation(value = "获取用户子订单列表", notes = "用户查看自己的所有历史订单，也可以根据订单状态来查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "请求的分页序列数", required = true, paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "请求的每一页数目", required = true, paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "status", value="订单状态查询", paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "token", value = "当前用户的token信息", required = true, paramType = "header", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code=200, message = "succeed", response = OrderVO.class, responseContainer = "List"),
            @ApiResponse(code=400, message = "please check your parameters again")
    })
    public ResponseEntity<Response<List<OrderVO>>> getOrderList(@RequestParam Integer pageNo,
                                                                @RequestParam Integer pageSize,
                                                                @RequestParam(required = false) Integer status,
                                                                @RequestParam(required = false) String storeId,
                                                                HttpServletRequest httpServletRequest) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        try {
            // 初始化status
            if (status == null){
                status = 0;
            }
            String userId = httpServletRequest.getHeader("userId");
            String role = httpServletRequest.getHeader("role");
            if (pageNo <= 0 || pageSize <= 0 || userId==null || status< 0 || status>5 || role==null){
                Response response = new Response(400, "Bad Request", null);
                return new ResponseEntity(response,headers,400);
            }
            if (Integer.parseInt(role) == 1) {
                Response response = new Response(401, "用户无查询权限", null);
                return new ResponseEntity(response,headers,401);
            }
            IPage<SubOrder> page = new Page<>(pageNo, pageSize);
            long[] total_num = new long[1];
            Response<List<OrderVO>> response = orderService.listSubOrders(page, status, userId, Integer.parseInt(role), storeId, total_num);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("total_num", total_num[0]);
            if (response.getData() == null){
                map.put("suborders", new ArrayList<>());
            }else{
                map.put("suborders",response.getData());
            }

            Response result = new Response(response.getStatus(), response.getDetail(), map);
            return new ResponseEntity(result,headers,response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().body(null);
    }

    @PutMapping("/subOrder")
    @ApiOperation(value="用户修改子订单", notes = "用户可以修改子订单的部分信息(物流信息)或者取消子订单，也可以删除子订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderVO", value = "用户提交订单后所有的订单信息", required = true, paramType = "body",dataType = "orderVO"),
            @ApiImplicitParam(name = "token", value = "当前用户的token信息", required = true, paramType = "header", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code=200, message = "succeed", response = SubOrder.class),
            @ApiResponse(code=400, message = "please check your parameters again")
    })
    public ResponseEntity<Response<OrderVO>> updateSubOrder(@RequestBody OrderVO orderVO,
                                                               HttpServletRequest httpServletRequest){
        MultiValueMap<String, String> headers = new HttpHeaders();
        String userId = null;
        try{
            userId = httpServletRequest.getHeader("userId");
        }catch (Exception e){
            return new ResponseEntity(null,headers,400);
        }
        if (orderVO == null || orderVO.getOrderItems() != null
                || orderVO.getTotalPrice()!=null || orderVO.getId() == null){
            return new ResponseEntity(null,headers,400);
        }
        SubOrder subOrder = new SubOrder();
        subOrder.setId(orderVO.getId());
        subOrder.setStatus(orderVO.getStatus());
        Response<SubOrder> response = orderService.updateSubOrder(subOrder, userId, orderVO.getConsignInfo());
        return new ResponseEntity(response,headers,response.getStatus());
    }


    @PostMapping()
    @ApiOperation(value = "创建用户订单", notes = "三张表都得改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderItems", value = "用户提交订单后所有的订单信息", required = true, paramType = "body", allowMultiple = true, type = "Map<String, Integer>"),
            @ApiImplicitParam(name = "token", value = "当前用户的token信息", required = true, paramType = "header", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code=200, message = "succeed", response = OrderVO.class),
            @ApiResponse(code=400, message = "please check your parameters again")
    })
    public ResponseEntity<Response<OrderVO>> CreateOrder(HttpServletRequest request, @RequestBody Map<String,Object> body){
        return orderService.CreateOrder(request, body);
        //return ResponseEntity.ok(null);
    }


    @PutMapping("/orderItem")
    @ApiOperation(value = "修改用户订单内具体条目", notes = "用户修改某一个orderItem的状态，例如将某一个OrderItem退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderItem", value = "返回修改后的OrderItem", required = true, paramType = "body", dataType = "OrderItem"),
            @ApiImplicitParam(name = "token", value = "当前用户的token信息", required = true, paramType = "header", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code=200, message = "succeed", response = OrderItem.class),
            @ApiResponse(code=400, message = "please check your parameters again")
    })
    public ResponseEntity<Response<OrderItem>> modifyOrder(@RequestBody OrderItem orderItem,
                                                           HttpServletRequest httpServletRequest
    ){
        MultiValueMap<String, String> headers = new HttpHeaders();
        String userId = null;
        try{
            userId = httpServletRequest.getHeader("userId");
        }catch (Exception e){
            return new ResponseEntity(null,headers,400);
        }
        Response<OrderItem> response = orderService.updateOrderItem(orderItem, userId);
        return new ResponseEntity(response,headers,response.getStatus());
    }

    @PutMapping("/subOrder/buyer/{subOrderId}")
    @ApiOperation(value = "买家修改订单状态", notes = "买家修改订单状态，包含取消，付款，完成")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subOrderId", value = "suborder id to pay", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "token", value = "当前用户的token信息", required = true, paramType = "header", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code=200, message = "succeed", response = OrderItem.class),
            @ApiResponse(code=400, message = "Bad Request"),
            @ApiResponse(code=404, message = "subOrder not exist"),
            @ApiResponse(code=409, message = "Money not enough")
    })
    public ResponseEntity<Response<SubOrder>> updateOrderByConsumer(@PathVariable String subOrderId,
                                                        HttpServletRequest httpServletRequest, @RequestBody Map<String, Integer> body
    ){
        if(body.isEmpty()){
            return new ResponseEntity("You must specify status of subOrder", null, 400);
        }
        MultiValueMap<String, String> headers = new HttpHeaders();
        Integer status = body.get("status");
        Response<SubOrder> response = orderService.updateOrderByConsumer(subOrderId, status);
        return new ResponseEntity(response,headers,response.getStatus());
    }

    @PutMapping("/payment/{orderId}")
    @ApiOperation(value = "对订单付款", notes = "对订单进行付款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "order id to pay", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "token", value = "当前用户的token信息", required = true, paramType = "header", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code=200, message = "succeed", response = OrderItem.class),
            @ApiResponse(code=404, message = "Order not exist"),
            @ApiResponse(code=409, message = "Money not enough")
    })
    public ResponseEntity<Response<OrderItem>> payOrder(@PathVariable String orderId,
                                                        HttpServletRequest httpServletRequest
    ){

        MultiValueMap<String, String> headers = new HttpHeaders();
        String userId = null;
        try{
            userId = httpServletRequest.getHeader("userId");
        }catch (Exception e){
            return new ResponseEntity(null,headers,400);
        }
        Response<Order> response =  orderService.payOrder(orderId);
        return new ResponseEntity(response,headers,response.getStatus());
    }

    @PutMapping("/subpayment/{subOrderId}")
    @ApiOperation(value = "对子订单付款", notes = "对子订单进行付款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subOrderId", value = "suborder id to pay", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "token", value = "当前用户的token信息", required = true, paramType = "header", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code=200, message = "succeed", response = OrderItem.class),
            @ApiResponse(code=404, message = "Order not exist"),
            @ApiResponse(code=409, message = "Money not enough")
    })
    public ResponseEntity<Response<OrderItem>> paySubOrder(@PathVariable String subOrderId,
                                                           HttpServletRequest httpServletRequest
    ){

        MultiValueMap<String, String> headers = new HttpHeaders();
        String userId = null;
        try{
            userId = httpServletRequest.getHeader("userId");
        }catch (Exception e){
            return new ResponseEntity(null,headers,400);
        }
        Response<SubOrder> response =  orderService.paySubOrder(subOrderId);
        return new ResponseEntity(response,headers,response.getStatus());
    }

    @PutMapping("/subOrder/seller/{sub_order_id}")
    public ResponseEntity<Response<SubOrder>> updateSellerSubOrder(@PathVariable String sub_order_id,
                                                                    @RequestBody SubOrder subOrder,
                                                                   HttpServletRequest httpServletRequest){
        MultiValueMap<String, String> headers = new HttpHeaders();
        String userId = null;
        try{
            userId = httpServletRequest.getHeader("userId");
        }catch (Exception e){
            return new ResponseEntity(null,headers,400);
        }
        Response<SubOrder> response = orderService.updateSellerSubOrder(sub_order_id, userId, subOrder.getLogisticsCompanyId(), subOrder.getStatus());
        return new ResponseEntity<>(response, headers, response.getStatus());
    }
}
