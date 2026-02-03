package com.arcfoxy.features.auth;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account extends PanacheEntity {

    public String username;
    public String passwordHash;
    public String socialClubId;
    public String hardwareId;
    public int adminLevel;

    public static Account findByLogin(String login) {
        return find("username", login).firstResult();
    }
}