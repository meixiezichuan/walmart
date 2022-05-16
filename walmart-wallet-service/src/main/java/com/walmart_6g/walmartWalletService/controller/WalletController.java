package com.walmart_6g.walmartWalletService.controller;

import com.walmart_6g.walmartWalletService.entity.Wallet;
import com.walmart_6g.walmartWalletService.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/hello")
    public String hello() {
        return "Welcome to Wallet Service!";
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWalletById(@PathVariable String id) {
        return walletService.getWalletById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWalletById(@PathVariable String id, @RequestBody Wallet wallet) {
        return walletService.updateWalletById(id, wallet);
    }
}
