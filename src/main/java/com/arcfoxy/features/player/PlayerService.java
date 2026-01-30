package com.arcfoxy.features.player;

import com.arcfoxy.core.GameRequest;
import com.arcfoxy.core.GameResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PlayerService {

    @Transactional
    public GameResponse login(GameRequest req) {
        System.out.println("👤 PLAYER SERVICE: Обрабатываю вход " + req.player);

        Player entity = Player.findByName(req.player);
        String text;

        if (entity == null) {
            entity = new Player();
            entity.username = req.player;
            entity.money = 1000;
            entity.persist();
            text = "Регистрация! Баланс: " + entity.money;
        } else {
            text = "С возвращением! Баланс: " + entity.money;
        }

        return new GameResponse("SHOW_NOTIFICATION", text);
    }
}