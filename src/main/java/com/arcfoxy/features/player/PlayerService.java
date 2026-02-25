package com.arcfoxy.features.player;

import com.arcfoxy.core.GameRequest;
import com.arcfoxy.core.GameResponse;
import com.arcfoxy.features.auth.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PlayerService {

    @Inject ObjectMapper json;

    private static final Logger LOG = LoggerFactory.getLogger(PlayerService.class);

    private static final float ZONE_MIN_X = 1200f;
    private static final float ZONE_MAX_X = 2800f;
    private static final float ZONE_MIN_Y = 2500f;
    private static final float ZONE_MAX_Y = 4200f;

    @Transactional
    public PlayerData createDefault(Long accountId) {
        PlayerData data = new PlayerData();
        data.accountId = accountId;
        data.money = 500;
        data.bankMoney = 0;
        data.health = 100;
        data.armor = 0;
        data.lastX = 1905.0f;
        data.lastY = 3715.0f;
        data.lastZ = 32.8f;
        data.persist();

        LOG.info("✅ Создан новый персонаж для accountId: {}", accountId);
        return data;
    }

    @Transactional
    public void savePlayer(GameRequest request) {
        try {
            PlayerData dto = json.readValue(request.data, PlayerData.class);

            PlayerData existing = PlayerData.findByAccountId(dto.accountId);

            if (existing == null) {
                LOG.warn("PlayerData не найден для accountId: {}", dto.accountId);
                return;
            }

            existing.lastX = dto.lastX;
            existing.lastY = dto.lastY;
            existing.lastZ = dto.lastZ;
            existing.health = dto.health;
            existing.armor = dto.armor;
            existing.money = dto.money;
            existing.bankMoney = dto.bankMoney;
            existing.persist();

            LOG.info("💾 Игрок сохранён: accountId {}", dto.accountId);

        } catch (Exception e) {
            LOG.error("SavePlayer error", e);
        }
    }

    public boolean isInSurvivalZone(float x, float y) {
        return x >= ZONE_MIN_X && x <= ZONE_MAX_X &&
                y >= ZONE_MIN_Y && y <= ZONE_MAX_Y;
    }
}