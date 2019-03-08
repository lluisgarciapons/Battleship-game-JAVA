package com.lluis.battleship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.ObjLongConsumer;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private PlayerRepository playerRepo;
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GamePlayerRepository gamePlayerRepo;
    @Autowired
    private ShipRepository shipRepo;
    @Autowired
    private SalvoRepository salvoRepo;
    @Autowired
    private ScoreRepository scoreRepo;

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
    public Map<String, Object> getGames() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player", loggedPlayer());
        dto.put("games", findGames());

        return dto;
    }

    private List<Map> findGames() {
        return gameRepo.findAll()
                .stream()
                .sorted((a, b) -> {
                    return (int)(b.getId() - a.getId());
                })
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

    private Map<String, Object> loggedPlayer() {
        return getPlayerFromAuth(findPlayerFromAuth());
    }

    private Player findPlayerFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            return playerRepo.findByUserName(username);
        }else {
            return null;
        }
    }

    private Map<String, Object> getPlayerFromAuth(Player player) {
        if (player != null) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", player.getId());
            dto.put("username", player.getUserName());
            dto.put("email", player.getEmail());

            return dto;
        }else {
            return null;
        }
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
                .sorted((a, b) -> {
                    return (int)(a.getId() - b.getId());
                })
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
        dto.put("email", gamePlayer.getPlayer().getEmail());

        return dto;
    }

    @RequestMapping("/game_view/{id}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable long id) {

        GamePlayer gp = gamePlayerRepo.getOne(id);
        if (gp != null) {
            Long gpPlayerId = gp.getPlayer().getId();
            if(findPlayerFromAuth().getId() != gpPlayerId) {
                return new ResponseEntity<> (makeMap("error", "Hey, are you trying to cheat? An automatic alert with" +
                        " your IP and physical address has been sent to the authorities, SWAT are coming."), HttpStatus.UNAUTHORIZED);
            }

            Map<String, Object> selectedGame = new LinkedHashMap<>();

            selectedGame.put("gameId", gp.getGame().getId());
            selectedGame.put("gameCreated", gp.getGame().getCreationDate());
            selectedGame.put("gamePlayers", findGamePlayer(gp.getGame().getGamePlayers()));
            selectedGame.put("ships", findShipsDTO(gp.getShips()));
            selectedGame.put("salvoes", getSalvoesDTO(findSalvoes(gp.getGame().getGamePlayers())));
            selectedGame.put("hitsAndSinks", getHitsDTO(gp.getGame().getGamePlayers()));
            return new ResponseEntity<>(selectedGame, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(makeMap("error", "This page doesn't exist"), HttpStatus.NOT_FOUND);
        }

    }

    private Map<Integer, Map<Long, Object>> getHitsDTO(Set<GamePlayer> gamePlayers) {
        Map<Integer, Map<Long, Object>> dto = new LinkedHashMap<>();
        Map<Long, Object> innerDTO;
        Set<Salvo> salvoes = findSalvoes(gamePlayers);

        for (Salvo salvo : salvoes) {
            if (!dto.containsKey(salvo.getTurn())) {
                innerDTO = new LinkedHashMap<>();
                innerDTO.put(salvo.getGamePlayer().getId(), HandS(salvo, salvoes, gamePlayers));
                dto.put(salvo.getTurn(), innerDTO);
            } else {
                innerDTO = dto.get(salvo.getTurn());
                innerDTO.put(salvo.getGamePlayer().getId(), HandS(salvo, salvoes, gamePlayers));
            }
        }
        return dto;
    }

    private Map<String, Object> HandS(Salvo salvo, Set<Salvo> salvoes, Set<GamePlayer> gamePlayers){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("hits", hitsDTO(salvo, gamePlayers));
        dto.put("damages", sinksDTO(salvo, salvoes, gamePlayers));
        return dto;
    }

    private Map<String, String> hitsDTO(Salvo salvo, Set<GamePlayer> gamePlayers) {
        Map<String, String> dto = new LinkedHashMap<>();
        GamePlayer opponent = getOpponent(gamePlayers, salvo.getGamePlayer().getId());

        for (String shot : salvo.getLocations()) {
            for (Ship ship : opponent.getShips()) {
                for (String shipLocation : ship.getLocations()) {
                    if (shot.equals(shipLocation)) {
                        dto.put(shot, ship.getType());
                    }
                }
            }
        }
        return dto;
    }

    private Map<String, Integer> sinksDTO(Salvo salvo, Set<Salvo> salvoes, Set<GamePlayer> gamePlayers){
        Map<String, Integer> dto = new LinkedHashMap<>();

        dto.put("Patrol Boat", 0);
        dto.put("Submarine", 0);
        dto.put("Destroyer", 0);
        dto.put("Battleship", 0);
        dto.put("Carrier", 0);

        Integer turns = salvo.getTurn();
        Long gameplayerId = salvo.getGamePlayer().getId();
        GamePlayer opponent = getOpponent(gamePlayers, gameplayerId);

        for (Salvo thisSalvo : salvoes) {
//            System.out.println(thisSalvo.getTurn());
            if (thisSalvo.getTurn() <= turns) {
                if(gameplayerId == thisSalvo.getGamePlayer().getId()){
                    for (String shot : thisSalvo.getLocations()) {
                        for (Ship ship : opponent.getShips()) {
                            for (String shipLocation : ship.getLocations()) {
                                if (shot.equals(shipLocation)) {
                                    dto.put(ship.getType(), dto.get(ship.getType()) + 1);
                                }
                            }
                        }
                    }
                }
            }
        }

        return dto;
    }

    private GamePlayer getOpponent(Set<GamePlayer> gamePlayers, Long gpId) {
        GamePlayer opponent = null;
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getId() != gpId){
                opponent = gamePlayer;
                break;
            } else {
                opponent = null;
            }
        }
        return opponent;
    }

    private Set<Salvo> findSalvoes(Set<GamePlayer> gamePlayers) {
        return gamePlayers
                .stream()
                .map(gamePlayer -> gamePlayer.getSalvoes())
                .flatMap(salvoSet -> salvoSet.stream())
                .sorted(Comparator.comparing(Salvo::getTurn))
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

    private GamePlayer whoIsTheHost(Game game) {
        GamePlayer host;
        Set<GamePlayer> gamePlayers = game.getGamePlayers();
        List<GamePlayer> bothGamePlayers = new LinkedList<>();
        for (GamePlayer gamePlayer : gamePlayers) {
            bothGamePlayers.add(gamePlayer);
        }
        if (bothGamePlayers.get(0).getId() < bothGamePlayers.get(1).getId()){
            host = bothGamePlayers.get(0);
        } else {
            host = bothGamePlayers.get(1);
        }
        return host;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String userName,
                                                          @RequestParam String email,
                                                          @RequestParam String password) {
        if (userName.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "no username provided."), HttpStatus.FORBIDDEN);
        }
        if (email.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "no email provided."), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepo.findByUserName(userName);
        Player player1 = playerRepo.findByEmail(email);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }
        if (player1 != null) {
            return new ResponseEntity<>(makeMap("error", "Email Already exists"), HttpStatus.CONFLICT);
        }

        Player newPlayer = playerRepo.save(new Player(userName, email, password));
        return new ResponseEntity<>(makeMap("userName", newPlayer.getUserName()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(){
        Map<String, Object> body;
        HttpStatus status;
        if (findPlayerFromAuth() != null) {
            Player thisPlayer = findPlayerFromAuth();
            Game newGame = gameRepo.save(new Game());
            GamePlayer newGp = gamePlayerRepo.save(new GamePlayer(thisPlayer, newGame));
            body = makeMap("gpid", newGp.getId());
            status = HttpStatus.CREATED;
        }  else {
            body = makeMap("error", "You must Log In first!");
            status = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(body, status);
    }

    @RequestMapping(path = "/game/{nn}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame (@PathVariable long nn) {
        Map<String, Object> body;
        HttpStatus status;
        if (findPlayerFromAuth() != null) {
            Player thisPlayer = findPlayerFromAuth();
            if (gameRepo.findById(nn) != null) {
                Game thisGame = gameRepo.getOne(nn);
                if (thisGame.getGamePlayers().size() == 1) {
                    if (!thisGame.getPlayers().contains(thisPlayer)) {
                        GamePlayer newGp = gamePlayerRepo.save(new GamePlayer(thisPlayer, thisGame));
                        body = makeMap("gpid", newGp.getId());
                        status = HttpStatus.CREATED;
                    } else {
                        body = makeMap("error", "You are already in this game.");
                        status = HttpStatus.FORBIDDEN;
                    }
                } else {
                    body = makeMap("error", "Game is full.");
                    status = HttpStatus.FORBIDDEN;
                }
            } else {
                body = makeMap("error", "No such game.");
                status = HttpStatus.FORBIDDEN;
            }
        } else {
            body = makeMap("error", "No user found.");
            status = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(body, status);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips (@PathVariable long gamePlayerId,
                                                           @RequestBody List<Ship> ships) {
        System.out.println(ships);
        Map<String, Object> body;
        HttpStatus status;
        Player thisPlayer = findPlayerFromAuth();
        if (thisPlayer != null) {
            GamePlayer gp = gamePlayerRepo.findOne(gamePlayerId);
            if (gp != null) {
                if (gp.getPlayer().getId() == thisPlayer.getId()) {
                    if (gp.getShips().size() == 0) {
                        for (Ship ship : ships) {
                            ship.setGamePlayer(gp);
                            shipRepo.save(ship);
                        }
                        body = makeMap("success", "All ships placed.");
                        status = HttpStatus.CREATED;
                    } else {
                        body = makeMap("error", "You have already placed your ships.");
                        status = HttpStatus.FORBIDDEN;
                    }
                } else {
                    body = makeMap("error", "You are not allowed see other player's ship locations.");
                    status = HttpStatus.UNAUTHORIZED;
                }
            } else {
                body = makeMap("error", "No game player with that Id.");
                status = HttpStatus.UNAUTHORIZED;
            }
        } else {
            body = makeMap("error", "No current user logged in.");
            status = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(body, status);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> shotSalvoes (@PathVariable long gamePlayerId,
                                                            @RequestBody Salvo salvo) {
        Map<String, Object> body;
        HttpStatus status;
        Player thisPlayer = findPlayerFromAuth();
        if (thisPlayer != null) {
            GamePlayer gp = gamePlayerRepo.findOne(gamePlayerId);
            if (gp != null) {
                if (gp.getPlayer().getId() == thisPlayer.getId()) {
                    if (salvo.getLocations().size() <= 5) {
//                        GamePlayer host = whoIsTheHost(gp.getGame());
                        int turn = gp.getSalvoes().size() + 1;

                        salvo.setGamePlayer(gp);
                        salvo.setTurn(turn);
                        salvoRepo.save(salvo);
                        body = makeMap("success", "salvoes shot.");
                        status = HttpStatus.CREATED;
                    } else {
                        body = makeMap("error", "Too many shots.");
                        status = HttpStatus.FORBIDDEN;
                    }
                } else {
                    body = makeMap("error", "You are not allowed see other player's view.");
                    status = HttpStatus.UNAUTHORIZED;
                }
            } else {
                body = makeMap("error", "No game player with that Id.");
                status = HttpStatus.UNAUTHORIZED;
            }
        } else {
            body = makeMap("error", "No current user logged in.");
            status = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(body, status);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}



