package com.lluis.battleship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

    private PlayerRepository playerRepo;
    private GameRepository gameRepo;
    private GamePlayerRepository gamePlayerRepo;
    private ShipRepository shipRepo;
    private SalvoRepository salvoRepo;
    private ScoreRepository scoreRepo;

    @Autowired
    AppController(PlayerRepository playerRepo,
                  GameRepository gameRepo,
                  GamePlayerRepository gamePlayerRepo,
                  ShipRepository shipRepo,
                  SalvoRepository salvoRepo,
                  ScoreRepository scoreRepo) {
        this.playerRepo = playerRepo;
        this.gameRepo = gameRepo;
        this.gamePlayerRepo = gamePlayerRepo;
        this.shipRepo = shipRepo;
        this.salvoRepo = salvoRepo;
        this.scoreRepo = scoreRepo;
    }

    @RequestMapping("/players")
    public List<Player> getPlayers(){
        return playerRepo.findAll();
    }

    @RequestMapping("/gamePlayers")
    public List<GamePlayer> getGamePlayers(){
        return gamePlayerRepo.findAll();
    }

    @RequestMapping("/ships")
    public List<Ship> getShips(){
        return shipRepo.findAll();
    }

    @RequestMapping("/salvoes")
    public List<Salvo> getSalvoes() {
        return salvoRepo.findAll();
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
        dto.put("gamePlayers", findGamePlayer(game.getGamePlayers()));
        dto.put("scores", findScores(game.getScores()));

        return dto;
    }

    private List<Map> findScores(Set<Score> scores) {

        return scores.stream()
                .map(item -> getScoresToDTO(item))
                .collect(Collectors.toList());
    }

    private Map<String, Object> getScoresToDTO(Score score) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", score.getId());
        dto.put("playerId", score.getPlayer().getId());
        dto.put("score", score.getScore());

        return dto;
    }

    private List<Map> findGamePlayer(Set<GamePlayer> gamePlayer) {

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

    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> getGameView(@PathVariable("nn") long id) {
        GamePlayer gp = gamePlayerRepo.getOne(id);
        Map<String, Object> selectedGame = new LinkedHashMap<>();

        selectedGame.put("gameId", gp.getGame().getId());
        selectedGame.put("gameCreated", gp.getGame().getCreationDate());
        selectedGame.put("gamePlayers", findGamePlayer(gp.getGame().getGamePlayers()));
        selectedGame.put("ships", findShipsDTO(gp.getShips()));
        selectedGame.put("salvoes", getSalvoesDTO(findSalvoesDTO(gp.getGame().getGamePlayers())));
         return selectedGame;
    }

    private Set<Salvo> findSalvoesDTO(Set<GamePlayer> gamePlayers) {
        return gamePlayers
                .stream()
                .map(gamePlayer -> gamePlayer.getSalvoes())
                .flatMap(salvoSet -> salvoSet.stream())
                .collect(Collectors.toSet());
    }

    private Map<Integer, Map> getSalvoesDTO(Set<Salvo> salvoes) {
        Map<Integer, Map> dto = new LinkedHashMap<>();
        Map<Long, Set> innerDTO;
        for (Salvo salvo : salvoes) {
            if (!dto.containsKey(salvo.getTurn())){
                innerDTO = new LinkedHashMap<>();
                innerDTO.put(salvo.getGamePlayer().getId(), salvo.getLocations());
                dto.put(salvo.getTurn(), innerDTO);
            } else {
                innerDTO = dto.get(salvo.getTurn());
                innerDTO.put(salvo.getGamePlayer().getId(), salvo.getLocations());
            }
        }

        return dto;
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


