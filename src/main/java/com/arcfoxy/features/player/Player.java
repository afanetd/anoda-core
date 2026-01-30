package com.arcfoxy.features.player;

import io.quarkus.hibernate.orm.panache.PanacheEntity; // Магия Quarkus
import jakarta.persistence.Entity; // Стандарт Java (JPA)

/**
 * @Entity говорит Кваркусу: "Это таблица в базе данных!"
 * extends PanacheEntity добавляет ID и методы (persist, find, list...)
 */
@Entity
public class Player extends PanacheEntity {

    public String username;

    public int money;

    public float x, y, z;

    public static Player findByName(String name) {
        return find("username", name).firstResult();
    }
}