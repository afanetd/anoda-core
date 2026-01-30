package com.arcfoxy.core;

import com.arcfoxy.features.player.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.context.ManagedExecutor;

@Startup
public class GameRouter {

    @Inject RedisDataSource redis;
    @Inject ReactiveRedisDataSource reactiveRedis;
    @Inject ObjectMapper json;
    @Inject ManagedExecutor executor;

    @Inject PlayerService playerService;

    public void init(@Observes StartupEvent ev) {
        System.out.println("🚦 ROUTER: Система запущена. Жду команды...");

        redis.pubsub(String.class).subscribe("game_events", message -> {
            executor.execute(() -> routeMessage(message));
        });
    }

    private void routeMessage(String message) {
        try {
            GameRequest req = json.readValue(message, GameRequest.class);
            GameResponse resp = null;

            switch (req.cmd) {
                case "login":
                    resp = playerService.login(req);
                    break;


                default:
                    System.out.println("⚠️ Неизвестная команда: " + req.cmd);
            }

            if (resp != null) {
                sendResponse(resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(GameResponse resp) {
        try {
            String jsonStr = json.writeValueAsString(resp);
            reactiveRedis.pubsub(String.class)
                    .publish("game_commands", jsonStr)
                    .subscribe().with(v -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}