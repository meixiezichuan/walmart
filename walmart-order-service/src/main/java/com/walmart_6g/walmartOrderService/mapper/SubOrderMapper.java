package com.walmart_6g.walmartOrderService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walmart_6g.walmartOrderService.entity.SubOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface SubOrderMapper extends BaseMapper<SubOrder> {
    List<Map<String, Object>> findSortedSubOrdersByOrdersId(Map<String, Object> map);
}
