package com.telegram.mafiabot.service;

import com.telegram.mafiabot.model.Player;
import com.telegram.mafiabot.repository.PlayersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayersService {

    private final PlayersRepository playersRepository;
    @Autowired
    public PlayersService(PlayersRepository playersRepository) {
        this.playersRepository = playersRepository;
    }

    public Optional<Player> findById(long chatId) {
        return playersRepository.findById(chatId);
    }

    public void save(Player player) {
        playersRepository.save(player);
    }

    public List<Player> findByUsername(String username) {
        return playersRepository.findByUsername(username);
    }


}
