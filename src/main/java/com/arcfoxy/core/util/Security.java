package com.arcfoxy.core.util;

import org.mindrot.jbcrypt.BCrypt;

public class Security {

    /**
     * Генерирует безопасный хеш с солью.
     * Используем при РЕГИСТРАЦИИ.
     * @param password "чистый" пароль (например, "123456")
     * @return строка длиной 60 символов (хеш + соль + настройки)
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Проверяет, подходит ли пароль к хешу.
     * Используем при ВХОДЕ (Login).
     * @param candidate "чистый" пароль, который ввел игрок
     * @param storedHash хеш из базы данных
     * @return true, если пароль верный
     */
    public static boolean checkPassword(String candidate, String storedHash) {
        if (storedHash == null || !storedHash.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(candidate, storedHash);
    }
}