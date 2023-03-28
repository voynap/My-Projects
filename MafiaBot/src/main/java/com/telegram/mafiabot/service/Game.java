package com.telegram.mafiabot.service;

import com.telegram.mafiabot.model.Player;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Game {

    private final MafiaBot mafiaBot;

    private List<Player> players = new ArrayList<>();

    protected Map<Integer, Player> playerMap = new HashMap<>();

    private final String key;

    private final long gameMasterChatId;
    public boolean isRolesSet = false;

    public boolean isReady = false;

    public Game(MafiaBot mafiaBot, String key, long gameMasterChatId) {
        this.mafiaBot = mafiaBot;
        this.key = key;
        this.gameMasterChatId = gameMasterChatId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getKey() {
        return key;
    }

    public long getGameMasterChatId() {
        return gameMasterChatId;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Map<Integer, Player> getPlayerMap() {
        return playerMap;
    }

    public void setPlayerMap(Map<Integer, Player> playerMap) {
        this.playerMap = playerMap;
    }

    public void prepare() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                gameInitialization();
            }
        };

        thread.start();
    }


    private void gameInitialization() {
            for (Player player : players) {
                if (!player.getName().equals("Guest"))
                    mafiaBot.sendMessage(player.getChatId(), "Пожалуйста, отправь мне свой номер");
            }


            List<Player> nonGuestPlayers = players.stream()
                    .filter(player -> !player.getName().equals("Guest"))
                    .toList();

            while (!(playerMap.values().containsAll(nonGuestPlayers))) {
                mafiaBot.sleep(1000); // ?? check if works
            }
            setPlayNumbersForGuests();
            mafiaBot.sendMessage(gameMasterChatId, "Используйте команду /numbers чтобы проверить игроков и их игровые номера");

            System.out.println(playerMap.toString());
            while (!isReady) {
                // Ждем пока ведущий нажмет кнопку продолжить
                // Continue button - ask for players to send their roles to bot
                mafiaBot.sleep(1000);
            }

            while (nonGuestPlayers.stream().anyMatch(player -> player.getRole() == null)) {
                System.out.println("LOOP");
                mafiaBot.sleep(500);
            }


            mafiaBot.sendMessage(gameMasterChatId, "Игрокам назначены роли, необходимо назначить роли гостям если такие есть.");

        //Игрокам назначены роли.

        for (Player player : players) {
            if (player.getName().equals("Guest")) {
                if (player.getRole() == null) {
                    SendMessage message = new SendMessage();
                    mafiaBot.rolesButtonInit(message, gameMasterChatId, "Назначьте роль игроку " + player.getUsername(), player);
                    try {
                        mafiaBot.execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        while (!isRolesSet) {
            mafiaBot.sleep(1000);
            if (players.stream().allMatch(p -> p.getRole() != null)) {
                isRolesSet = true;
            }
        }

        for (Player player : players) {
            System.out.println(player.getName() + player.getRole());
        }

        synchronizeRolesInMapAndList();

        mafiaBot.sendMessage(gameMasterChatId, "Вы можете в любой момент узнать игроков и их роли используя команду /players");


        }

    private void synchronizeRolesInMapAndList() {
        for (Player mapPlayer : playerMap.values()) {
            for (Player listPlayer : players ) {
                if (mapPlayer.equals(listPlayer)) {
                    mapPlayer.setRole(listPlayer.getRole());
                    break;
                }
            }
        }
    }

    protected void checkPlayersAndNumbers() {
        String playersAndNumbersMessage = generatePlayerAndNumbersMessage(isRolesSet);
        mafiaBot.sendMessage(gameMasterChatId, playersAndNumbersMessage);
        SendMessage sendMessage = new SendMessage();
        mafiaBot.oneButtonInit(sendMessage, gameMasterChatId, "Проверьте, что все правильно", "Продолжить", "CONTINUE_BUTTON");
        mafiaBot.sendMessage(gameMasterChatId, "Используйте /numbers чтобы обновить список");
        try {
            mafiaBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }



    public void start() {




        }

    public String generatePlayerAndNumbersMessage(boolean isRolesSet) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, Player> player : playerMap.entrySet()) {
            builder
                    .append("Игрок ")
                    .append(player.getKey())
                    .append(" : ")
                    .append(player.getValue().getName());
            if (isRolesSet) {
                builder.append(" -> ").append(player.getValue().getRole());
            }
                builder.append("\n");
        }
        return builder.toString();
    }


    private void setPlayNumbersForGuests() {
        for (Player player : players) {
            if (player.getName().equals("Guest")) {
                for (int i = 1; i <=15 ; i++) {
                    if (!playerMap.containsKey(i)) {
                        player.setUsername("Player" + i);
                        playerMap.put(i, player);
                        break;
                    }
                }
            }
        }
    }

    public void sendMessageToAll(String text) {
        for (Player player : players) {
            mafiaBot.sendMessage(player.getChatId(), text);
        }
    }


}
