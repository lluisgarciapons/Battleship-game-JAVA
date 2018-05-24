package com.lluis.battleship;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private Date date;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new LinkedHashSet<>();

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<Score> scores = new LinkedHashSet<>();

    public void addGamePlayer(GamePlayer gamePlayer) {
//        gamePlayer.setGame(this);     don't need this line because the Game is already set in the GamePlayer constructor.
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score) {
        scores.add(score);
    }

    public Game() {
        date = new Date();
    }

    public long getId() {
        return id;
    }

    public Date getCreationDate() {
        return date;
    }

    public void setCreationDate(Date date) {
        this.date = date;
    }

    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    @JsonIgnore
    public List<Player> getPlayers() {
        return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }
}
