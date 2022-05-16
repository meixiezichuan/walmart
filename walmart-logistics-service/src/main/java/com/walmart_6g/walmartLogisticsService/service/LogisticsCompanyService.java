package com.walmart_6g.walmartLogisticsService.service;


import entity.LogisticsCompany;
import entity.Response;

import java.util.List;

public interface LogisticsCompanyService {

    public Response<LogisticsCompany> insert(LogisticsCompany logisticsCompany);

    public Response<List<LogisticsCompany>> listAll();

    public Response<LogisticsCompany> listById(String companyId);

    public Response<List<LogisticsCompany>> listByIds(List<String> ids);
}
