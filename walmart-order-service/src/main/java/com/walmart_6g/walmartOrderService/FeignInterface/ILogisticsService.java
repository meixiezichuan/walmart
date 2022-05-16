package com.walmart_6g.walmartOrderService.FeignInterface;

import entity.LogisticsCompany;
import entity.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * Created on 2021/12/3  5:07 下午
 *
 * @author yang hong
 */
@FeignClient(name = "walmart-logistics-service",url = "${logistics-service.uri}")
public interface ILogisticsService {
    @GetMapping("/api/v1/logistics_company/{company_id}")
    public ResponseEntity<Response<LogisticsCompany>> getLogisticsCompanyById(@PathVariable String company_id);
    @PostMapping("/api/v1/logistics_company/ids")
    public ResponseEntity<Response<List<LogisticsCompany>>> getLogisticsCompaniesByIds(@RequestBody Map<String, Object> company_ids);
}
