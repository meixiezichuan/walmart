package com.walmart_6g.walmartWalletService.service.impl;

import com.walmart_6g.walmartWalletService.entity.Wallet;
import com.walmart_6g.walmartWalletService.mapper.WalletMapper;
import com.walmart_6g.walmartWalletService.service.WalletService;
import entity.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletMapper walletMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Override
    public ResponseEntity<Response> getWalletById(String id) {
        LOGGER.info("[{}]开始运行","getWalletById");
        Wallet wallet = walletMapper.findById(id);
        Map<String, Float> data = new HashMap<>();
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser_id(id);
            wallet.setBalance(0);
            try {
                walletMapper.save(wallet);
                data.put("balance", wallet.getBalance());
                return ResponseEntity.ok(new Response<>(200, "SUCCESS", data));
            } catch (Exception e) {
                LOGGER.error("get wallet by id failed: "  + e.toString());
                return ResponseEntity.status(400).body(new Response<>(404, "SOMETHING WRONG", null));
            }
        } else {
            data.put("balance", wallet.getBalance());
            return ResponseEntity.ok(new Response<>(200, "SUCCESS", data));
        }
    }

    @Override
    public ResponseEntity<Response> updateWalletById(String id, Wallet wallet) {
        LOGGER.info("[{}]开始运行","updateWalletById");
        Wallet oldWallet = walletMapper.findById(id);
        Map<String, Float> data = new HashMap<>();
        if (oldWallet == null) {
            oldWallet = new Wallet();
            oldWallet.setUser_id(id);
            oldWallet.setBalance(0);
            try {
                walletMapper.save(oldWallet);
                if (wallet.getAmount() >= 0) {
                    try {
                        oldWallet.setBalance(wallet.getAmount());
                        walletMapper.save(oldWallet);
                        data.put("balance", oldWallet.getBalance());
                        return ResponseEntity.ok(new Response<>(200, "SUCCESS", data));
                    } catch (Exception e) {
                        LOGGER.error("update wallet by id failed: "  + e.toString());
                        return ResponseEntity.status(400).body(new Response<>(400, "FAILED", null));
                    }
                } else {
                    return ResponseEntity.status(409).body(new Response<>(409, "NO MORE MONEY", null));
                }
            } catch (Exception e) {
                LOGGER.error("update wallet by id failed: "  + e.toString());
                return ResponseEntity.status(400).body(new Response<>(404, "SOMETHING WRONG", null));
            }
        }
        float balance = oldWallet.getBalance() + wallet.getAmount();
        if (balance < 0) {
            return ResponseEntity.status(409).body(new Response<>(409, "NO MORE MONEY", null));
        }
        oldWallet.setBalance(balance);
        try {
            walletMapper.save(oldWallet);
            data.put("balance", oldWallet.getBalance());
            return ResponseEntity.ok(new Response<>(200, "SUCCESS", data));
        } catch (Exception e) {
            LOGGER.error("update wallet by id failed: "  + e.toString());
            return ResponseEntity.status(400).body(new Response<>(400, "FAILED", null));
        }
    }
}
