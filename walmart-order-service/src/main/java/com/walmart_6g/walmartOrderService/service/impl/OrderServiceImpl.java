package com.walmart_6g.walmartOrderService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart_6g.walmartOrderService.FeignInterface.IGoodsService;
import com.walmart_6g.walmartOrderService.FeignInterface.ILogisticsService;
import com.walmart_6g.walmartOrderService.FeignInterface.IUserService;
import com.walmart_6g.walmartOrderService.FeignInterface.IWalletService;
import com.walmart_6g.walmartOrderService.entity.Order;
import com.walmart_6g.walmartOrderService.entity.OrderItem;
import com.walmart_6g.walmartOrderService.entity.SubOrder;
import com.walmart_6g.walmartOrderService.entity.*;
import com.walmart_6g.walmartOrderService.mapper.AddressMapper;
import com.walmart_6g.walmartOrderService.mapper.OrderItemMapper;
import com.walmart_6g.walmartOrderService.mapper.OrderMapper;
import com.walmart_6g.walmartOrderService.mapper.SubOrderMapper;
import com.walmart_6g.walmartOrderService.service.OrderService;
import com.walmart_6g.walmartOrderService.vo.OrderVO;
import entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    public static String adminId = "";

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ILogisticsService iLogisticsService;

    @Autowired
    private IGoodsService iGoodsService;

    @Autowired
    private IWalletService iWalletService;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SubOrderMapper subOrderMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    GoodsCategory getGC(String gcId) {
        LOGGER.info("[{}]开始运行","getGC");
        Object oGC = iGoodsService.getGoodCateByID(gcId).getBody().getData();
        ObjectMapper goodCateObjectMapper = new ObjectMapper();
        GoodsCategory goodsCategory= goodCateObjectMapper.convertValue(oGC, GoodsCategory.class);
        return goodsCategory;
    }

    String getAdminId() {
        LOGGER.info("[{}]开始运行","getAdminId");
        if(adminId != "") {
            return adminId;
        }
        try{
            ResponseEntity<Response> res = iUserService.getAdmin();
            if(res.getStatusCodeValue() != 200 ) {
                return adminId;
            }
            Object u = res.getBody().getData();
            ObjectMapper userObjectMapper = new ObjectMapper();
            User admin = userObjectMapper.convertValue(u, User.class);
            adminId = admin.getId();
        }catch(Exception e){
            LOGGER.error("get adminId failed: "+ e.toString());
        }
        return adminId;
    }

    @Override
    public List<SubOrder> getSubOrderByOrderId(String orderId) {
        LOGGER.info("[{}]开始运行","getSubOrderByOrderId");
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("deprecated", false).eq("order_id", orderId);
        List<SubOrder> subOrders = subOrderMapper.selectList(wrapper);
        return subOrders;
    }


    @Override
    public ResponseEntity<Response<OrderVO>> CreateOrder(HttpServletRequest request, Map<String,Object> body) {
        LOGGER.info("[{}]开始运行","CreateOrder");
        //需要返回的数据
        OrderVO orderVO=new OrderVO();
        List<OrderItem> items=new ArrayList<OrderItem>();
        //疯狂拿id
        String addressId=UUID.randomUUID().toString();
        String orderId=UUID.randomUUID().toString();
        //拿到创建订单的用户id
        String token = request.getHeader("token");
        Object oU = iUserService.getUserByToken(token).getBody().getData();
        ObjectMapper userObjectMapper = new ObjectMapper();
        User user = userObjectMapper.convertValue(oU, User.class);

        if(user==null){
            return ResponseEntity.status(400).body(new Response<>(400, "USER DOES NOT EXIST", null));
        }

        String userId=user.getId();
        //拿到当前时间
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Timestamp createTime = new Timestamp(new Date(System.currentTimeMillis()).getTime());
        //获取前端信息
        Map<String,Integer> orderItems=(Map<String,Integer>)body.get("orderItems");
        if(orderItems==null){
            return ResponseEntity.status(400).body(new Response<>(400, "USER DOES NOT ORDER ANYTHING", null));
        }

//        addressFromV addressInfo=(addressFromV)body.get("address");
        Object oAI=body.get("address");
        ObjectMapper addressFromVObjectMapper=new ObjectMapper();
        addressFromV addressInfo=addressFromVObjectMapper.convertValue(oAI,addressFromV.class);

        if(addressInfo==null){
            return ResponseEntity.status(400).body(new Response<>(400, "PLEASE COMPLETE ADDRESS INFO", null));
        }

        orderVO.setConsignInfo(addressInfo.getConsignInfo());

        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("name",addressInfo.getName())
                .eq("consign_info",addressInfo.getConsignInfo())
                .eq("phone_num",addressInfo.getPhoneNum());
        List<Address> addressList = addressMapper.selectList(addressQueryWrapper);
        if(addressList.size()==0){
            //创建新的Address项
            Address address=new Address();
            address.setName(addressInfo.getName());
            address.setConsignInfo(addressInfo.getConsignInfo());
            address.setPhoneNum(addressInfo.getPhoneNum());
            address.setDeprecated(false);
            address.setUserId(userId);
            address.setId(addressId);
            addressMapper.insert(address);
        } else {
            // if address already exist, use old address id
            addressId = addressList.get(0).getId();
        }
        //先存一部分Order,还差totalPrice
        Order order=new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setCreateTime(createTime);
        order.setAddressId(addressId);
        //UtoS是商户id和subOrderId的对应关系
        Map<String, String> MtoS = new HashMap<String, String>();
        //StoP是subOrderId和总价的对应关系，总价用Float计算
        Map<String, Float> StoP = new HashMap<String, Float>();
        //遍历商品,先存一部分orderItem
        float totalPrice=0;
        List<String> categoryIds = new ArrayList<>();
        for(String goodCateId: orderItems.keySet()) {
            categoryIds.add(goodCateId);
        }
        HashMap<String, List<String>> reqBody = new HashMap<>();
        reqBody.put("categoryList", categoryIds);
        ResponseEntity<Response> response =iGoodsService.getGoodsDetailsByCategoryIds(reqBody);
        if (response.getStatusCodeValue() != 200 ){
            return ResponseEntity.status(response.getStatusCodeValue()).body(response.getBody());
        }

        try {
            Object o = response.getBody().getData();
            List<Object> gds = (List<Object>) o;
            HashMap<String, Float> storePrice = new HashMap();
            HashMap<String, String> storeSubOrder = new HashMap();
            HashMap<String, String> storeSeller = new HashMap();
            ObjectMapper objectMapper = new ObjectMapper();
            for (Object og : gds) {
                GoodsDetail gd = objectMapper.convertValue(og, GoodsDetail.class);
                String categoryId = gd.getCategoryId();
                String storeId = gd.getStoreId();
                float price = gd.getPrice() * orderItems.get(categoryId);
                if(storeSubOrder.get(storeId) == null) {
                    storeSubOrder.put(storeId, UUID.randomUUID().toString());
                }
                if(storePrice.get(storeId) == null){
                    storePrice.put(storeId, price);
                }else{
                    storePrice.put(storeId, price+storePrice.get(storeId));
                }
                totalPrice += price;

                if(storeSeller.get(storeId) == null){
                    storeSeller.put(storeId, gd.getSellerId());
                }

                // create orderItem
                OrderItem orderItem = new OrderItem(UUID.randomUUID().toString(), storeSubOrder.get(storeId), gd.getGoodsId(), categoryId,
                        gd.getGoodsName(), "", gd.getGoodsImage(), gd.getCategoryName(), 2, orderItems.get(categoryId),
                        gd.getPrice(), false);
                items.add(orderItem);
                orderItemMapper.insert(orderItem);
            }
            for(String storeId: storeSubOrder.keySet()){
                SubOrder subOrder = new SubOrder(storeSubOrder.get(storeId), orderId, storeId, SubOrder.unPayed,
                        storePrice.get(storeId), addressId, false, storeSeller.get(storeId), "");
                subOrderMapper.insert(subOrder);
                updateGoodsStock(storeSubOrder.get(storeId));
            }
        }catch (Exception e){
            LOGGER.error("create order failed: "  + e.toString());
            return ResponseEntity.status(500).body(new Response<>(500,"Network error",null));
        }

        orderVO.setId(orderId);//返回主订单的id
        orderVO.setStatus(SubOrder.unPayed);
        orderVO.setTotalPrice(totalPrice);
        orderVO.setOrderItems(items);
        order.setTotalPrice(totalPrice);
        orderMapper.insert(order);

        //表已建好，现在封装返回前端数据
        return ResponseEntity.status(200).body(new Response<>(200, "CREATE ORDER SUCCESS", orderVO));
    }

    @Override
    public Response<List<OrderVO>> listSubOrders(IPage<SubOrder> iPage, int status, String userId, int role, String storeId, long[] total_num){
        LOGGER.info("[{}]开始运行","listSubOrders");
        Response<List<OrderVO>> response = new Response();
        try{
            // 无论是否查询到都会返回200
            response.setStatus(200);
            List<Order> orders = null;
            List<String> orderIds = new LinkedList<>();
            if (role == User.customer_role){
                // 买家查询自己的订单
                QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
                orderQueryWrapper.eq("user_id", userId);
                orders = orderMapper.selectList(orderQueryWrapper);
                if (orders==null || orders.size()==0){
                    response.setDetail("Order list is empty!");
                    response.setData(null);
                    return response;
                }
                // 买家的所有Order封装成OrderIds
                for (Order order:orders){
                    orderIds.add(order.getId());
                }
                QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
                if (status != 0){
                    wrapper.eq("status", status);
                }
                wrapper.eq("deprecated", false);
                wrapper.in("order_id", orderIds);
                total_num[0] = subOrderMapper.selectCount(wrapper);
            }
            if (role == User.seller_role){
                // 卖家查询自己所有商铺的订单
                QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
                Response response1 = iGoodsService.queryStore(userId).getBody();
                Map<String, Object> map = (Map<String, Object>) response1.getData();
                List<Map<String, String>> storeList = (List<Map<String, String>>) map.get("stores");
                if (storeList==null || storeList.size() == 0){
                    response.setDetail("该用户无商铺");
                    return response;
                }
                List<String> store_ids = new ArrayList<>();
                for (Map<String, String> store:storeList){
                    store_ids.add(store.get("id"));
                }
                wrapper.in("store_id", store_ids);
                if (status != 0){
                    wrapper.eq("status", status);
                }
                wrapper.eq("deprecated", false);
                List<SubOrder> subOrders = subOrderMapper.selectList(wrapper);
                if (subOrders==null || subOrders.size() == 0) {
                    response.setDetail("SubOrder list is empty!");
                    response.setData(null);
                    return response;
                }
                // 卖家的所有order封装成OrderIds
                for (SubOrder subOrder:subOrders){
                    if (!orderIds.contains(subOrder.getOrderId())){
                        orderIds.add(subOrder.getOrderId());
                    }
                }
                total_num[0] = subOrders.size();
            }
            if (orderIds.size()==0){
                response.setDetail("Order list is empty!");
                response.setData(null);
                return response;
            }else{
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("status", status);
                queryMap.put("start", (iPage.getCurrent()-1)*iPage.getSize());
                queryMap.put("end", iPage.getSize());
                queryMap.put("orderIds", orderIds);
                List<Map<String, Object>> suborders = subOrderMapper.findSortedSubOrdersByOrdersId(queryMap);
                if (suborders == null || suborders.size() == 0){
                    response.setDetail("SubOrder list is empty!");
                    response.setData(null);
                    return response;
                }
                response.setDetail("succeed");
                // 查询子订单的OrderItems
                List<String> ids = new ArrayList<>();
                for (Map<String, Object> subOrder:suborders){
                    ids.add((String) subOrder.get("id"));
                }
                QueryWrapper<OrderItem> orderItemQueryWrapper = new QueryWrapper<>();
                orderItemQueryWrapper.eq("deprecated", false).in("sub_order_id", ids);
                List<OrderItem> orderItems = orderItemMapper.selectList(orderItemQueryWrapper);
                List<OrderVO> orderVOS = new ArrayList<>();
                List<String> stores_ids = new LinkedList<>();
                List<String> address_ids = new ArrayList<>();
                List<Object> storeList = new ArrayList<>();
                List<Address> addressList = new LinkedList<>();
                List<String> logistics_ids = new LinkedList<>();
                List<LogisticsCompany> logisticsCompanyList = new ArrayList<>();
                for (Map<String, Object> subOrder:suborders){
                    stores_ids.add((String) subOrder.get("store_id"));
                    if (!((String) subOrder.get("logistics_company_id")).equals("")){
                        logistics_ids.add((String) subOrder.get("logistics_company_id"));
                    }
                    if (subOrder.get("logistics_company_id")!=null){
                        address_ids.add((String) subOrder.get("address_id"));
                    }
                }
                // 查询storeIds对应的store信息
                HashMap<String, List<String>> storesMap = new HashMap<>();
                storesMap.put("storeList", stores_ids);
                ResponseEntity<Response> goodsResponse = iGoodsService.getStoresByIDs(storesMap);
                if (goodsResponse == null || goodsResponse.getBody() == null){
                    return new Response<>(406, "No response for iGoodsService.getStoresByIDs", null);
                }
                if (goodsResponse.getBody().getStatus()!=200){
                    return new Response<>(406, "Query error for iGoodsService.getStoresByIDs", null);
                }else {
                    storeList = (List<Object>) goodsResponse.getBody().getData();
                }
                // 查询logistics_ids对应的logistics信息
                if (logistics_ids.size() != 0){
                    Map<String, Object> logisticsMap = new HashMap<>();
                    logisticsMap.put("company_ids", logistics_ids);
                    ResponseEntity<Response<List<LogisticsCompany>>> logisticsResponse = iLogisticsService.getLogisticsCompaniesByIds(logisticsMap);
                    if (logisticsResponse == null || logisticsResponse.getBody() == null){
                        return new Response<>(406, "No response for iLogisticsService.getLogisticsCompaniesByIds", null);
                    }
                    if (logisticsResponse.getBody().getStatus()!=200){
                        return new Response<>(406, "Query error for iLogisticsService.getLogisticsCompaniesByIds", null);
                    }else {
                        logisticsCompanyList = logisticsResponse.getBody().getData();
                    }
                }
                // 查询address_ids对应的address信息
                if (address_ids.size() != 0){
                    addressList = addressMapper.selectBatchIds(address_ids);
                }
                System.out.println(addressList);
                // 将address的id和address对应在一起
                Map<String, Address> addressIdMap =new HashMap<>();
                for (Address address:addressList){
                    addressIdMap.put(address.getId(), address);
                }
                // 将store的id和store对应在一起
                Map<String, Store> storeIdMap = new HashMap<>();
                for (Object store:storeList){
                    ObjectMapper objectMapper = new ObjectMapper();
                    Store s = objectMapper.convertValue(store, Store.class);
                    storeIdMap.put(s.getId(), s);
                }
                // 将LogisticsCompany的id和LogisticsCompany对应在一起
                Map<String, LogisticsCompany> logisticsCompanyIdMap = new HashMap<>();
                for (LogisticsCompany logisticsCompany:logisticsCompanyList){
                    logisticsCompanyIdMap.put(logisticsCompany.getId(), logisticsCompany);
                }
                // 封装OrderVO
                for (Map<String, Object> subOrder:suborders){
                    List<OrderItem> orderItemsInsert = new ArrayList<>();
                    String logistics_name = "";
                    LogisticsCompany logisticsCompany = logisticsCompanyIdMap.getOrDefault((String) subOrder.get("logistics_company_id"), null);
                    if (logisticsCompany != null){
                        logistics_name = logisticsCompany.getName();
                    }
                    String addressInfo = "";
                    Address address = addressIdMap.getOrDefault((String) subOrder.get("address_id"),null);
                    if (address != null){
                        addressInfo = address.getConsignInfo();
                    }
                    for (OrderItem orderItem:orderItems){
                        if (orderItem.getSubOrderId().equals(subOrder.get("id"))){
                            orderItemsInsert.add(orderItem);
                        }
                    }
                    LocalDateTime create_time = (LocalDateTime) subOrder.get("create_time");
                    OrderVO orderVO = new OrderVO((String) subOrder.get("id"), (Integer) subOrder.get("status"),
                            (Float) subOrder.get("total_price"), addressInfo, orderItemsInsert, create_time.toInstant(ZoneOffset.of("-8")).toString(),
                            (String) subOrder.get("address_id"), (String) subOrder.get("store_id"), storeIdMap.get((String) subOrder.get("store_id")).getName(),
                            (String) subOrder.get("logistics_company_id"), logistics_name, false);
                    orderVOS.add(orderVO);
                }
                response.setData(orderVOS);
            }
        }catch (Exception e){
            LOGGER.error("list subOrders failed: "+ e.toString());
            // 服务器报错
            response.setStatus(500);
            response.setDetail(e.toString());
            System.out.println(e);
        }
        return response;
    }


    @Override
    public Response<SubOrder> updateSubOrder(SubOrder subOrder, String userId, String consignInfo) {
        LOGGER.info("[{}]开始运行","updateSubOrder");
        if(userId.equals("")){
            return new Response<>(401, "UPDATE FAILED FOR NO ACCESS", null);
        }
        if (!verifyBySubOrderUserId(subOrder, userId)){
            return new Response<>(401, "UPDATE FAILED FOR SUBORDER DATA ERROR", null);
        }
        SubOrder subOrderData = subOrderMapper.selectById(subOrder.getId());
        // 修改地址
        if (consignInfo != null){
            if (subOrderData == null){
                return new Response<>(406, "UPDATE FAILED FOR SUBORDER DATA ERROR", null);
            }
            Address address = addressMapper.selectById(subOrderData.getAddressId());
            if (address == null){
                return new Response<>(406, "Address not Exists", null);
            }else{
                address.setConsignInfo(consignInfo);
                address.setId(null);
                int success = addressMapper.insert(address);
                if (success == 0){
                    return new Response<>(406, "Address update error", null);
                }else{
                    subOrder.setAddressId(address.getId());
                }
            }
        }
        // 修改subOrder
        int status = subOrderMapper.updateById(subOrder);
        if (status ==0){
            return new Response<>(406, "UPDATE FAILED FOR SUBORDER DATA ERROR", null);
        }
        SubOrder result = subOrderMapper.selectById(subOrder.getId());
        return new Response<SubOrder>(200, "SUCCESS", result);
    }


    @Override
    public Response<OrderItem> updateOrderItem(OrderItem orderItem, String userId) {
        LOGGER.info("[{}]开始运行","updateOrderItem");
        if(userId.equals("")){
            return new Response<>(406, "UPDATE FAILED FOR NO ACCESS", null);
        }
        if (!verifyByOrderItemUserId(orderItem, userId)){
            return new Response<>(406, "UPDATE FAILED FOR ORDERITEM DATA ERROR", null);
        }
        int status = orderItemMapper.updateById(orderItem);
        if (status == 0){
            return new Response<>(406, "UPDATE FAILED FOR ORDERITEM DATA ERROR", null);
        }
        SubOrder subOrderData = subOrderMapper.selectById(orderItem.getSubOrderId());
        subOrderData.setTotalPrice(subOrderData.getTotalPrice() - orderItem.getPrice()*orderItem.getNum());
        subOrderMapper.updateById(subOrderData);
        OrderItem orderItemData = orderItemMapper.selectById(orderItem.getId());
        return new Response<OrderItem>(200, "SUCCESS", orderItemData);
    }

    @Override
    public Response<SubOrder> updateOrderByConsumer(String subOrderId, Integer status){
        LOGGER.info("[{}]开始运行","updateOrderByConsumer");
        List<Integer> avaliableStatus = Arrays.asList(SubOrder.cancelled, SubOrder.unDelivered, SubOrder.completed);
        if(!avaliableStatus.contains(status)) {
            return new Response<SubOrder>(400, "Status update by consumer not allowed", null);
        }
        switch(status.intValue()) {
            case SubOrder.unDelivered:
                // pay suborder
                return paySubOrder(subOrderId);
            case SubOrder.cancelled:
                // cancel suborder
                return cancelSubOrderByConsumer(subOrderId);
            case SubOrder.completed:
                // complete suborder
                return completeSubOrderByConsumer(subOrderId);
            default:
                return new Response<SubOrder>(400, "Status update by consumer not allowed", null);
        }
    }

    @Override
    @Transactional
    public Response<Order> payOrder(String orderId) {
        LOGGER.info("[{}]开始运行","payOrder");
        Order orderData = orderMapper.selectById(orderId);
        if (orderData == null) {
            return new Response<Order>(404, "order not exist", null);
        }
        // To reduce concurrency，we pay total order at once
        // TODO: if we need transaction flow, then need pay suborder each
//        Boolean payed = Pay(orderData.getUserId(), orderData.getTotalPrice());
//        if (payed) {
//            return new Response<Order>(200, "Payed order succeed", null);
//        } else {
//            return new Response<Order>(409, "Payed order Failed", null);
//        }

        Float balance = GetBalance(orderData.getUserId());
        if(balance.compareTo(orderData.getTotalPrice()) < 0) {
            return new Response<Order>(409, "Payed order Failed, You don't have enough money", null);
        }
        QueryWrapper<SubOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("deprecated", false).eq("order_id", orderId);
        List<SubOrder> subOrders = subOrderMapper.selectList(wrapper);
        if(subOrders.isEmpty()) {
            return new Response<Order>(404, "No suborder in this order", null);
        }
        for(SubOrder subOrder: subOrders) {
            paySubOrder(subOrder.getId());
         }
        return new Response<Order>(200, "Payed order succeed", orderData);
    }

    @Override
    public Response<SubOrder> paySubOrder(String subOrderId){
        LOGGER.info("[{}]开始运行","paySubOrder");
        SubOrder subOrderData = subOrderMapper.selectById(subOrderId);
        if(subOrderData == null ){
            return new Response<SubOrder>(404, "suborder not exist", subOrderData);
        }
        if(subOrderData.getStatus() != SubOrder.unPayed) {
            return new Response<SubOrder>(400, "Can not pay, status is not unPayed", subOrderData);
        }
        Order order = orderMapper.selectById(subOrderData.getOrderId());
        if(order == null ){
            return new Response<SubOrder>(404, "order not exist", subOrderData);
        }
        float amount = subOrderData.getTotalPrice();
        String userId = order.getUserId();

        Boolean payed = Pay(userId, amount);

        if(payed) {
            subOrderData.setStatus(SubOrder.unDelivered);
            subOrderMapper.updateById(subOrderData);
            // notify seller
            String content = "订单[" + subOrderId + "]：用户已付款，请尽快安排发货";
            sendDirectionMessage(subOrderData.getSellerId(), getAdminId(), content);
            return new Response<SubOrder>(200, "Payed suborder Succeed", subOrderData);
        }else {
            return new Response<SubOrder>(409, "Payed suborder Failed", subOrderData);
        }
    }

    public Response<SubOrder> cancelSubOrderByConsumer(String subOrderId){
        LOGGER.info("[{}]开始运行","cancelSubOrderByConsumer");
        SubOrder subOrderData = subOrderMapper.selectById(subOrderId);
        if(subOrderData == null ){
            return new Response<SubOrder>(404, "suborder not exist", subOrderData);
        }
        if(subOrderData.getStatus() != SubOrder.unPayed) {
            return new Response<SubOrder>(400, "Can not cancel, status is not unPayed", subOrderData);
        }

        // update goods stock
        updateGoodsStock(subOrderId);
        // update status to cancelled
        subOrderData.setStatus(SubOrder.cancelled);
        subOrderMapper.updateById(subOrderData);
        // notify seller
        String content = "订单[" + subOrderId + "]： 用户已取消";
        sendDirectionMessage(subOrderData.getSellerId(), getAdminId(), content);
        return new Response<SubOrder>(200, "Suborder Cancelled by Consumer Succeed", subOrderData);
    }

    public Response<SubOrder> completeSubOrderByConsumer(String subOrderId){
        LOGGER.info("[{}]开始运行","completeSubOrderByConsumer");
        SubOrder subOrderData = subOrderMapper.selectById(subOrderId);
        if(subOrderData == null ){
            return new Response<SubOrder>(404, "suborder not exist", subOrderData);
        }
        if(subOrderData.getStatus() != SubOrder.unReceived) {
            return new Response<SubOrder>(400, "Can not cancel, status is not unReceived", subOrderData);
        }

        // pay to seller
        String seller = subOrderData.getSellerId();
        float amount = subOrderData.getTotalPrice();
        Boolean payed = Pay(seller, (0-amount));
        if(!payed) {
            return new Response<SubOrder>(409, "Complete suborder Failed", subOrderData);
        }
        // update status to completed
        subOrderData.setStatus(SubOrder.completed);
        subOrderMapper.updateById(subOrderData);
        // notify seller
        String content = "订单[" + subOrderId + "]： 用户已确认收货";
        sendDirectionMessage(seller, getAdminId(), content);
        return new Response<SubOrder>(200, "Suborder completed by Consumer Succeed", subOrderData);
    }

    public Response<SubOrder> cancelSubOrderBySeller(String subOrderId){
        LOGGER.info("[{}]开始运行","cancelSubOrderBySeller");
        SubOrder subOrderData = subOrderMapper.selectById(subOrderId);
        if(subOrderData == null ){
            return new Response<SubOrder>(404, "suborder not exist", subOrderData);
        }
        if(subOrderData.getStatus() != SubOrder.unDelivered) {
            return new Response<SubOrder>(400, "Can not cancel, status is not unDelivered", subOrderData);
        }

        Order order = orderMapper.selectById(subOrderData.getOrderId());
        if(order == null ){
            return new Response<SubOrder>(404, "order not exist", subOrderData);
        }
        float amount = subOrderData.getTotalPrice();
        String userId = order.getUserId();

        // refund
        Boolean payed = Pay(userId, (0-amount));
        if(!payed) {
            return new Response<SubOrder>(409, "Payed suborder Failed", subOrderData);
        }
        // update goods stock
        updateGoodsStock(subOrderId);
        // update status to cancelled
        subOrderData.setStatus(SubOrder.cancelled);
        subOrderMapper.updateById(subOrderData);

        // todo notify consumer
        return new Response<SubOrder>(200, "Suborder Cancelled by Merchant Succeed", subOrderData);
    }

    @Transactional
    public Boolean Pay(String userId, float amount) {
        LOGGER.info("[{}]开始运行","Pay");
        // check adminId
        String adminId = getAdminId();
        if(adminId == "") {
            return false;
        }


        Wallet wallet = new Wallet();
        wallet.setAmount(0 - amount);
        try {
            //  money of user
            ResponseEntity<Response> response = iWalletService.pay(userId, wallet);
            if(response.getStatusCodeValue() != 200) {
                return false;
            }
            // money of admin(system)
            wallet.setAmount(amount);
            ResponseEntity<Response> resp = iWalletService.pay(adminId, wallet);
            if(resp.getStatusCodeValue() == 200) {
                return true;
            }else {
                iWalletService.pay(userId, wallet);
                return false;
            }
        }catch (Exception e) {
            LOGGER.error("payed failed: " + e.toString());
        }
        return false;
    }

    public Float GetBalance(String userId){
        LOGGER.info("[{}]开始运行","GetBalance");
        Float balance = -1.0f;
        try {
            ResponseEntity<Response> response = iWalletService.getBalance(userId);
            if(response.getStatusCodeValue() != 200) {
                return balance;
            }
            Object o = response.getBody().getData();
            ObjectMapper oMapper = new ObjectMapper();
            Map<String, Double> wa = oMapper.convertValue(o, Map.class);

            if(wa.get("balance") == null ){
                return balance;
            }
            float fa = wa.get("balance").floatValue();
            balance = fa;
        }catch (Exception e) {
            LOGGER.error("get user wallet failed: " + e.toString());
        }

        return balance;
    }

    public Response<?> updateGoodsStock(String sub_order_id) {
        LOGGER.info("[{}]开始运行","updateGoodsStock");
        QueryWrapper<OrderItem> orderItemQueryWrapper = new QueryWrapper<>();
        orderItemQueryWrapper.eq("sub_order_id", sub_order_id).eq("deprecated", false);
        List<OrderItem> orderItems = orderItemMapper.selectList(orderItemQueryWrapper);
        for(OrderItem o: orderItems){
            GoodsCategory gc = getGC(o.getGoodsCategoryId());
            gc.setNum(gc.getNum()-o.getNum());
            ResponseEntity<Response> response = iGoodsService.UpdateGC(gc.getId(), gc);
            if(response.getStatusCodeValue() != 200 ) {
                return new Response<SubOrder>(409, "Goods stock update failed", null);
            }
        }
        return new Response<SubOrder>(200, "Goods stock update succeed", null);
    }



    public Response<?> reverseUpdateGoodsStock(String sub_order_id) {
        LOGGER.info("[{}]开始运行","reverseUpdateGoodsStock");
        QueryWrapper<OrderItem> orderItemQueryWrapper = new QueryWrapper<>();
        orderItemQueryWrapper.eq("sub_order_id", sub_order_id).eq("deprecated", false);
        List<OrderItem> orderItems = orderItemMapper.selectList(orderItemQueryWrapper);
        for(OrderItem o: orderItems){
            GoodsCategory gc = getGC(o.getGoodsCategoryId());
            gc.setNum(gc.getNum()+o.getNum());
            ResponseEntity<Response> response = iGoodsService.UpdateGC(gc.getId(), gc);
            if(response.getStatusCodeValue() != 200 ) {
                return new Response<SubOrder>(409, "Goods stock update failed", null);
            }
        }
        return new Response<SubOrder>(200, "Goods stock update succeed", null);
    }

    public boolean verifyBySubOrderUserId(SubOrder subOrder, String userId){
        LOGGER.info("[{}]开始运行","verifyBySubOrderUserId");
        String subOrderId = subOrder.getId();
        SubOrder subOrderData = subOrderMapper.selectById(subOrderId);
        if (subOrderData == null){
            return Boolean.FALSE;
        }else{
            Order orderData = orderMapper.selectById(subOrderData.getOrderId());
            if (orderData == null){
                return Boolean.FALSE;
            }
            if (!orderData.getUserId().equals(userId)){
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public boolean verifyByOrderItemUserId(OrderItem orderItem, String userId){
        LOGGER.info("[{}]开始运行","verifyByOrderItemUserId");
        String orderItemId = orderItem.getId();
        OrderItem orderItemData = orderItemMapper.selectById(orderItemId);
        if (orderItemData == null){
            return Boolean.FALSE;
        }else {
            //todo 判断orderItemData和orderItem，看是否修改了不应该修改的属性
            if (!orderItemData.getNum().equals(orderItem.getNum())){
                return Boolean.FALSE;
            }
            if (!orderItemData.getSubOrderId().equals(orderItem.getSubOrderId())){
                return Boolean.FALSE;
            }
            if (!orderItemData.getPrice().equals(orderItem.getPrice())){
                return Boolean.FALSE;
            }
            if (!orderItemData.getGoodsCategoryName().equals(orderItem.getGoodsCategoryName())){
                return Boolean.FALSE;
            }
            if (!orderItemData.getGoodsDescription().equals(orderItem.getGoodsDescription())){
                return Boolean.FALSE;
            }
            if (!orderItemData.getGoodsId().equals(orderItem.getGoodsId())){
                return Boolean.FALSE;
            }
            if (!orderItemData.getGoodsImage().equals(orderItem.getGoodsImage())){
                return Boolean.FALSE;
            }
            if (!orderItemData.getGoodsName().equals(orderItem.getGoodsName())){
                return Boolean.FALSE;
            }
            SubOrder subOrder = subOrderMapper.selectById(orderItem.getSubOrderId());
            if (subOrder == null){
                return Boolean.FALSE;
            }return verifyBySubOrderUserId(subOrder, userId);
        }
    }


    @Override
    public Response<SubOrder> updateSellerSubOrder(String subOrderId, String sellerId, String logisticsCompanyId, int status) {
        LOGGER.info("[{}]开始运行","updateSellerSubOrder");
        if(sellerId.equals("")){
            return new Response<>(401, "无用户", null);
        }
        SubOrder subOrder = subOrderMapper.selectById(subOrderId);
        if(subOrder==null){
            return new Response<>(401, "subOrderId错误", null);
        }
        Order order = orderMapper.selectById(subOrder.getOrderId());
        if(order==null){
            return new Response<>(401, "subOrderId错误", null);
        }
        String buyerId = order.getUserId();
        String storeId = subOrder.getStoreId();
        ResponseEntity<Response> responseStore = iGoodsService.queryStoreByStoreId(storeId);
        if(responseStore.getBody().getStatus()!=200){
            return new Response<>(406, "subOrderId错误", null);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Store store = objectMapper.convertValue(responseStore.getBody().getData(), Store.class);
        if(store==null){
            return new Response<>(406, "该子订单所属商店不存在", null);
        }
        //判断改卖家是否能更新此订单
        if(!store.getUserId().equals(sellerId)){
            return new Response<>(406, "此卖家不能更新此订单", null);
        }
        int statusData = subOrder.getStatus();

        String admin_id = getAdminId();
        //根据status更新钱包以及发送通知
        if (statusData==SubOrder.unDelivered&&status==SubOrder.cancelled){
            if(Pay(buyerId, -subOrder.getTotalPrice())){
                String content = "订单[" + subOrderId + "]： 商家已取消";
                //更新库存
                reverseUpdateGoodsStock(subOrderId);
                sendDirectionMessage(buyerId, admin_id, content);
            }else{
                return new Response<>(401, "钱包更新时更新失败", null);
            }
        } else if(statusData==SubOrder.unDelivered&&status==SubOrder.unReceived){
            if (logisticsCompanyId==null||logisticsCompanyId.equals("")){
                return new Response<>(401, "物流信息不能为空", null);
            }
            String content = "订单[" + subOrderId + "]： 商家已发货，请及时留意物流信息";
            sendDirectionMessage(buyerId, admin_id, content);
        }else {
            return new Response<>(401, "status数据错误", null);
        }
        //更改订单信息
        subOrder.setStatus(status);
        subOrder.setLogisticsCompanyId(logisticsCompanyId);
        int is_ok = subOrderMapper.updateById(subOrder);
        if (is_ok==1){
            return new Response<>(200, "更新成功", subOrder);
        }
        //更新失败的话数据还原
        Pay(buyerId, subOrder.getTotalPrice());
        updateGoodsStock(subOrderId);
        return new Response<>(406, "更新失败", null);
    }

    @Override
    public Boolean sendDirectionMessage(String receiver, String sender, String content) {
        LOGGER.info("[{}]开始运行","sendDirectionMessage");
        String messageId = String.valueOf(UUID.randomUUID());
        Map<String,Object> map=new HashMap<>();
        map.put("messageId", messageId);
        map.put("content", content);
        map.put("sender", sender);
        map.put("receiver", receiver);
        rabbitTemplate.convertAndSend("DirectExchange", "DirectRouting", map);
        return true;
    }

}
