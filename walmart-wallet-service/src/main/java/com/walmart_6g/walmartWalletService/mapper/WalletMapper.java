package com.walmart_6g.walmartWalletService.mapper;

import com.walmart_6g.walmartWalletService.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletMapper extends JpaRepository<Wallet, Long> {
    @Query(value = "SELECT * FROM wallet WHERE user_id=?1", nativeQuery = true)
    Wallet findById(String id);
}
