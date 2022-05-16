package com.walmart_6g.walmartWalletService.service;

import com.walmart_6g.walmartWalletService.entity.Wallet;
import entity.Response;
import org.springframework.http.ResponseEntity;


public interface WalletService {
    ResponseEntity<Response> getWalletById(String id);

    ResponseEntity<Response> updateWalletById(String id, Wallet wallet);
}
