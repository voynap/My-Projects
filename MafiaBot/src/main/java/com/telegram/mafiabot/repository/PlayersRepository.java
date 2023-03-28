package com.telegram.mafiabot.repository;

import com.telegram.mafiabot.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayersRepository extends JpaRepository<Player, Long> {
    List<Player> findByUsername(String player);


}
