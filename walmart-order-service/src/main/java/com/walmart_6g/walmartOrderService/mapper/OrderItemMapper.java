package com.walmart_6g.walmartOrderService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walmart_6g.walmartOrderService.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
