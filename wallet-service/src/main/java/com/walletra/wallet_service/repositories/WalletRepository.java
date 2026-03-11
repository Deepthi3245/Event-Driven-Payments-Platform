package com.walletra.wallet_service.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.walletra.wallet_service.models.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

}
