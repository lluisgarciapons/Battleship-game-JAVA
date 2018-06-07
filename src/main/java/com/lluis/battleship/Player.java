package com.lluis.battleship;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String userName;
    private String email;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new LinkedHashSet<>();

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<Score> scores = new LinkedHashSet<>();

    private String password;

    public Player() { }

    public Player(String userName, String email, String password) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
//        gamePlayer.setPlayer(this);       don't need this line because the Player is already set in the GamePlayer constructor.
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score) {
        scores.add(score);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    @JsonIgnore
    public List<Game> getGames() {
        return gamePlayers.stream().map(sub -> sub.getGame()).collect(toList());
    }

    @JsonIgnore
    public Set<Score> getScores() {
        return scores;
    }
}

