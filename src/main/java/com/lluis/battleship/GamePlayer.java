package com.lluis.battleship;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships = new LinkedHashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Salvo> salvoes = new LinkedHashSet<>();

    public GamePlayer() {}

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        player.addGamePlayer(this);
        game.addGamePlayer(this);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    public void addShips(Ship ship) {
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        salvoes.add(salvo);
    }

    public long getId() {
        return id;
    }
}
