package com.walmart_6g.walmartLogisticsService.controller;

import com.walmart_6g.walmartLogisticsService.service.LogisticsCompanyService;
import entity.LogisticsCompany;
import entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/29 13:04
 * @Description:
 */
@RestController
@RequestMapping("api/v1/logistics_company")
public class LogisticsCompanyController {

    @Autowired
    private LogisticsCompanyService logisticsCompanyService;

    @PostMapping("")
    public ResponseEntity<Response<LogisticsCompany>> createLogisticsCompany(@RequestBody LogisticsCompany logisticsCompany){
        MultiValueMap<String, String> headers = new HttpHeaders();
        Response<LogisticsCompany> response = logisticsCompanyService.insert(logisticsCompany);
        return new ResponseEntity<>(response, headers, response.getStatus());
    }

    @GetMapping("")
    public ResponseEntity<Response<List<LogisticsCompany>>> getAllLogisticsCompany(){
        MultiValueMap<String, String> headers = new HttpHeaders();
        Response<List<LogisticsCompany>> response = logisticsCompanyService.listAll();
        return new ResponseEntity<>(response, headers, response.getStatus());
    }

    @GetMapping("/{company_id}")
    public ResponseEntity<Response<LogisticsCompany>> getLogisticsCompanyById(@PathVariable String company_id){
        MultiValueMap<String, String> headers = new HttpHeaders();
        System.out.println(company_id);
        Response<LogisticsCompany> response = logisticsCompanyService.listById(company_id);
        return new ResponseEntity<>(response, headers, response.getStatus());
    }

    @PostMapping("/ids")
    public ResponseEntity<Response<List<LogisticsCompany>>> getLogisticsCompaniesByIds(@RequestBody Map<String, Object> company_ids){
        List<String> ids = (List<String>) company_ids.get("company_ids");
        MultiValueMap<String, String> headers = new HttpHeaders();
        Response<List<LogisticsCompany>> response = logisticsCompanyService.listByIds(ids);
        return new ResponseEntity<>(response, headers, response.getStatus());
    }
}