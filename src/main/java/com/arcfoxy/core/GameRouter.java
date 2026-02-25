package com.arcfoxy.core;

import com.arcfoxy.features.auth.AuthService;
import com.arcfoxy.features.player.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
public class GameRouter {

    private static final Logger LOG = LoggerFactory.getLogger(GameRouter.class);

    @Inject RedisDataSource redis;
    @Inject ReactiveRedisDataSource reactiveRedis;
    @Inject ObjectMapper json;
    @Inject ManagedExecutor executor;

    @Inject AuthService authService;
    @Inject PlayerService playerService;

    public void init(@Observes StartupEvent ev) {
        LOG.info("🚦 ROUTER: Система запущена. Жду команды...");

        redis.pubsub(String.class).subscribe("game_events", message -> {
            executor.execute(() -> routeMessage(message));
        });
    }

    private void routeMessage(String message) {
        try {
            LOG.info("📨 RAW MESSAGE FROM REDIS: {}", message);

            GameRequest req = json.readValue(message, GameRequest.class);

            if (req.data == null) {
                LOG.error("❌ ОШИБКА ПАРСИНГА: Поле 'data' == null! Проверь имена полей в index.ts");
                return; 
            }

            GameResponse resp = null;

            switch (req.cmd) {
                case "login":
                    resp = authService.login(req);
                    break;
                case "register":
                    resp = authService.register(req);
                    break;
                case "save_player":
                    playerService.savePlayer(req);
                    break;
                default:
                    LOG.warn("⚠️ Неизвестная команда: {}", req.cmd);
            }

            if (resp != null) {
                sendResponse(resp);
            }

        } catch (Exception e) {
            LOG.error("🔥 Ошибка обработки сообщения: ", e);
        }
    }

    private void sendResponse(GameResponse resp) {
        try {
            String jsonStr = json.writeValueAsString(resp);
            reactiveRedis.pubsub(String.class)
                    .publish("game_commands", jsonStr)
                    .subscribe().with(v -> {});
        } catch (Exception e) {
            LOG.error("❌ Ошибка отправки ответа в Redis: ", e);
        }
    }
}