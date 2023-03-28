package com.telegram.mafiabot.model;

import com.telegram.mafiabot.LocationGame.Location;
import com.telegram.mafiabot.service.Game;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity(name = "playersData")
@Data
public class Player {
    @Id
    private long chatId;
    private String name;

    private Role role;

    private int score;

    private String username;

    private String gameKey;

    private boolean isMaster = false;

    public Player() {

    }

    public Player(int chatId, String name) {
        this.chatId = chatId;
        this.name = name;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (chatId != player.chatId) return false;
        if (isMaster != player.isMaster) return false;
        if (!name.equals(player.name)) return false;
        return username.equals(player.username);
    }

    @Override
    public int hashCode() {
        int result = (int) (chatId ^ (chatId >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + (isMaster ? 1 : 0);
        return result;
    }
}



