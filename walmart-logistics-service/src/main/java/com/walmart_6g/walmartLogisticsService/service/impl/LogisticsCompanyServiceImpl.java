package com.walmart_6g.walmartLogisticsService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.walmart_6g.walmartLogisticsService.mapper.LogisticsCompanyMapper;
import com.walmart_6g.walmartLogisticsService.service.LogisticsCompanyService;
import entity.LogisticsCompany;
import entity.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/29 12:52
 * @Description:
 */
@Service
public class LogisticsCompanyServiceImpl implements LogisticsCompanyService {

    @Autowired
    private LogisticsCompanyMapper logisticsCompanyMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(LogisticsCompanyServiceImpl.class);

    @Override
    public Response<LogisticsCompany> insert(LogisticsCompany logisticsCompany) {
        LOGGER.info("[{}]开始运行","insert");
        if(logisticsCompany.getName().equals("")){
            return new Response<>(406, "名称不能为空", null);
        }
        String id = UUID.randomUUID().toString();
        logisticsCompany.setId(id);
        logisticsCompany.setDeprecated(0);
        int status = logisticsCompanyMapper.insert(logisticsCompany);
        if(status==1){
            return new Response<>(200, "新增成功", logisticsCompany);
        }
        return new Response<>(406, "插入失败", null);

    }

    @Override
    public Response<List<LogisticsCompany>> listAll() {
        LOGGER.info("[{}]开始运行","listAll");
        QueryWrapper<LogisticsCompany> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deprecated",0);
        List<LogisticsCompany> logisticsCompanies = logisticsCompanyMapper.selectList(queryWrapper);
        return new Response<>(200, "查询成功", logisticsCompanies);
    }

    @Override
    public Response<LogisticsCompany> listById(String companyId) {
        LOGGER.info("[{}]开始运行","listById");
        if (companyId.equals("")){
            return new Response<>(406, "companyId不能为空", null);
        }
        QueryWrapper<LogisticsCompany> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",companyId);
        List<LogisticsCompany> logisticsCompanies = logisticsCompanyMapper.selectList(queryWrapper);
        if(logisticsCompanies.size()==0){
            return new Response<>(406, "companyId错误", null);
        }
        return new Response<>(200, "查询成功", logisticsCompanies.get(0));
    }

    @Override
    public Response<List<LogisticsCompany>> listByIds(List<String> ids) {
        LOGGER.info("[{}]开始运行","listByIds");

        if (ids.size()==0){
            return new Response<>(406, "companyIds不能为空", null);
        }
        QueryWrapper<LogisticsCompany> queryWrapper = new QueryWrapper<>();

        queryWrapper.in("id", ids);
        List<LogisticsCompany> logisticsCompanies = logisticsCompanyMapper.selectList(queryWrapper);
        if(logisticsCompanies.size()==0){
            return new Response<>(400, "companyIds错误", null);
        }
        return new Response<>(200, "查询成功", logisticsCompanies);
    }
}