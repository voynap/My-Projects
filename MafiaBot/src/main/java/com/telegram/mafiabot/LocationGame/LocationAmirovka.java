package com.telegram.mafiabot.LocationGame;

import com.telegram.mafiabot.model.Player;


import java.util.ArrayList;
import java.util.List;
public class LocationAmirovka implements Location {


    List<Player> signedUpPlayers = new ArrayList<>();

    @Override
    public void signUp(Player player) {
        signedUpPlayers.add(player);
    }
}
