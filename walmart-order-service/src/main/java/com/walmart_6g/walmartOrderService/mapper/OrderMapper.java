package com.walmart_6g.walmartOrderService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walmart_6g.walmartOrderService.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
