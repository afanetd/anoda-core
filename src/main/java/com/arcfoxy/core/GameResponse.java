package com.arcfoxy.core;

public class GameResponse {
    public String action;
    public String data;

    public GameResponse() {}

    public GameResponse(String action, String data) {
        this.action = action;
        this.data = data;
    }
}