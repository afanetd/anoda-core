package com.arcfoxy.features.player;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class PlayerData extends PanacheEntity {

    public Long accountId;

    public int money;
    public int bankMoney;

    public float lastX;
    public float lastY;
    public float lastZ;

    public int health;
    public int armor;
}