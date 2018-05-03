package com.lluis.battleship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Id;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

    private PlayerRepository playerRepo;
    private GameRepository gameRepo;
    private GamePlayerRepository gamePlayerRepo;

    @Autowired
    AppController(PlayerRepository playerRepo, GameRepository gameRepo, GamePlayerRepository gamePlayerRepo) {
        this.playerRepo = playerRepo;
        this.gameRepo = gameRepo;
        this.gamePlayerRepo = gamePlayerRepo;
    }

    @RequestMapping("/players")
    public List<Player> getPlayers(){
        return playerRepo.findAll();
    }

    @RequestMapping("/games")
    public List<Map> getGames(){
        return gameRepo.findAll()
                .stream()
                .map(game -> getGamesDTO(game))
                .collect(Collectors.toList());
    }

    private Map<String, Object> getGamesDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", getGamePlayerDTO(game.getGamePlayers()));

        return dto;
    }

    private List<Map> getGamePlayerDTO(Set<GamePlayer> gamePlayer) {

        return gamePlayer
                .stream()
                .map(item -> getItemsDTO(item))
                .collect(Collectors.toList());
    }

    private Map<String, Object> getItemsDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", playerItemsDTO(gamePlayer));

        return dto;
    }

    private Map<String, Object> playerItemsDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getPlayer().getId());
        dto.put("username", gamePlayer.getPlayer().getUserName());

        return dto;
    }

    @RequestMapping("/gamePlayers")
    public List<GamePlayer> getGamePlayers(){
        return gamePlayerRepo.findAll();
    }
}
