package com.lluis.battleship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
    private ShipRepository shipRepo;

    @Autowired
    AppController(PlayerRepository playerRepo,
                  GameRepository gameRepo,
                  GamePlayerRepository gamePlayerRepo,
                  ShipRepository shipRepo) {
        this.playerRepo = playerRepo;
        this.gameRepo = gameRepo;
        this.gamePlayerRepo = gamePlayerRepo;
        this.shipRepo = shipRepo;
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
        dto.put("gamePlayers", findGamePlayerDTO(game.getGamePlayers()));

        return dto;
    }

    private List<Map> findGamePlayerDTO(Set<GamePlayer> gamePlayer) {

        return gamePlayer
                .stream()
                .map(item -> getGamePlayerDTO(item))
                .collect(Collectors.toList());
    }

    private Map<String, Object> getGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", playerToDTO(gamePlayer));

        return dto;
    }

    private Map<String, Object> playerToDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getPlayer().getId());
        dto.put("username", gamePlayer.getPlayer().getUserName());

        return dto;
    }

    @RequestMapping("/gamePlayers")
    public List<GamePlayer> getGamePlayers(){
        return gamePlayerRepo.findAll();
    }

    @RequestMapping("/ships")
    public List<Ship> getShips(){
        return shipRepo.findAll();
    }

    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> getGameView(@PathVariable("nn") long id) {
        GamePlayer gp = gamePlayerRepo.getOne(id);
        Map<String, Object> selectedGame = new LinkedHashMap<>();

        selectedGame.put("gameId", gp.getGame().getId());
        selectedGame.put("gameCreated", gp.getGame().getCreationDate());
        selectedGame.put("gamePlayers", findGamePlayerDTO(gp.getGame().getGamePlayers()));
        selectedGame.put("ships", findShipsDTO(gp.getShips()));

         return selectedGame;
    }

    private List<Map> findShipsDTO(Set<Ship> ships) {
        return ships
                .stream()
                .map(ship -> getShipsToDTO(ship))
                .collect(Collectors.toList());
    }

    private Map<String, Object> getShipsToDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", ship.getType());
        dto.put("location", ship.getLocations());

        return dto;
    }
}


