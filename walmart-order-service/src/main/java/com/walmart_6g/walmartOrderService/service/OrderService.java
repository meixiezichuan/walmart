package com.walmart_6g.walmartOrderService.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.walmart_6g.walmartOrderService.vo.OrderVO;
import com.walmart_6g.walmartOrderService.entity.*;

import entity.Response;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author yanghong
 */
public interface OrderService {
    ResponseEntity<Response<OrderVO>> CreateOrder(HttpServletRequest request, @RequestBody Map<String,Object> body);

    /**
     * 列举某个用户的所有子订单
     * @param iPage 分页查询
     * @param status 订单状态
     * @param userId 用户ID
     * @return List<OrderVo>
     */
    Response<List<OrderVO>> listSubOrders(IPage<SubOrder> iPage, int status, String userId, int role, String storeId, long[] total_num);

    Response<SubOrder> updateSubOrder(SubOrder subOrder, String userId, String consignInfo);

    Response<OrderItem> updateOrderItem(OrderItem orderItem, String userId);

    Response<SubOrder> updateOrderByConsumer(String subOrderId, Integer status);

    Response<SubOrder> paySubOrder(String subOrderId);
    Response<Order> payOrder(String orderId);

    public List<SubOrder> getSubOrderByOrderId(String orderId);

    public Response<SubOrder> updateSellerSubOrder(String subOrderId, String sellerId, String logisticsCompanyId, int status);

    public Boolean sendDirectionMessage(String receiver, String sender, String content);
}

