package com.walmart_6g.walmartLogisticsService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import entity.LogisticsCompany;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/29 12:41
 * @Description:
 */

@Mapper
@Repository
public interface LogisticsCompanyMapper extends BaseMapper<LogisticsCompany> {

}