package com.arcfoxy.features.auth;

import com.arcfoxy.core.GameRequest;
import com.arcfoxy.core.GameResponse;
import com.arcfoxy.features.auth.dto.AuthDTO; // <--- ПЕРЕИМЕНОВАЛИ (бывший RegistrationDTO)
import com.arcfoxy.features.auth.dto.LoginResponseDTO;
import com.arcfoxy.features.player.PlayerData; // Не забудь импортировать!
import com.arcfoxy.features.player.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static com.arcfoxy.core.util.Security.checkPassword;
import static com.arcfoxy.core.util.Security.hashPassword;

@ApplicationScoped
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    @Inject ObjectMapper json;
    @Inject PlayerService playerService;
	 
    @Transactional
    public GameResponse login(GameRequest request) {
        try {
	    System.out.println("DEBUG: пришел JSON: " + request.data);
            AuthDTO dto = json.readValue(request.data, AuthDTO.class);

            LOG.info("Попытка входа игрока: {}", dto.username);

            Account account = Account.findByLogin(dto.username);

            if (account == null) {
                return errorResponse("Пользователь не найден");
            }

            boolean isValid = checkPassword(dto.password, account.passwordHash);
            if (!isValid) {
                return errorResponse("Неверный пароль");
            }

            PlayerData stats = PlayerData.find("accountId", account.id).firstResult();

            if (stats == null) {
                LOG.warn("У игрока {} нет данных PlayerData! Создаю временные.", account.username);
                stats = playerService.createDefault(account.id);
            }

            return successResponse(account, stats, request.player);

        } catch (Exception e) {
            LOG.error("Login error", e);
            return errorResponse("Server Error");
        }
    }

    @Transactional
    public GameResponse register(GameRequest request) {
        try {
            AuthDTO dto = json.readValue(request.data, AuthDTO.class);
            LOG.info("Регистрация нового игрока: {}", dto.username);

            Account existingAccount = Account.findByLogin(dto.username);

            if (existingAccount != null) {
                return errorResponse("Этот логин уже занят!");
            }

            Account newAccount = new Account();
            newAccount.username = dto.username;
            newAccount.socialClubId = dto.socialClubId;
            newAccount.hardwareId = dto.hardwareId;
            newAccount.passwordHash = hashPassword(dto.password);

            newAccount.persist();

            PlayerData stats = playerService.createDefault(newAccount.id);

            return successResponse(newAccount, stats, request.player);

        } catch (Exception e) {
            LOG.error("Registration error", e);
            return errorResponse("Ошибка при регистрации");
        }
    }

    private GameResponse successResponse(Account account, PlayerData stats, String playerHandle) {
        try {
            LoginResponseDTO dto = new LoginResponseDTO();
            dto.status = "success";
            dto.username = account.username;
            dto.playerHandle = playerHandle;
            dto.characterData = stats;

            GameResponse response = new GameResponse();
            response.action = "AUTH_SUCCESS";
            response.data = json.writeValueAsString(dto);

            return response;
        } catch (Exception e) {
            return errorResponse("Ошибка упаковки данных");
        }
    }

    private GameResponse errorResponse(String reason) {
        return new GameResponse("AUTH_ERROR", "{\"reason\": \"" + reason + "\"}");
    }
}