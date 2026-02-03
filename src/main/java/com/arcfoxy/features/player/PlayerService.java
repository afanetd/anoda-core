package com.arcfoxy.features.player;

import com.arcfoxy.core.GameRequest;
import com.arcfoxy.core.GameResponse;
import com.arcfoxy.features.auth.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PlayerService {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerService.class);

    @Transactional
    public PlayerData createDefault(Long accountId) {
        PlayerData data = new PlayerData();

        data.accountId = accountId;
        data.money = 1000;
        data.bankMoney = 0;
        data.health = 100;
        data.armor = 0;

        data.lastX = 228.0f;
        data.lastY = -900.0f;
        data.lastZ = 30.0f;

        // Сохраняем в базу данных
        data.persist();

        return data;
    }
}