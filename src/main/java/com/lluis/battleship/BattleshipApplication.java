package com.lluis.battleship;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class BattleshipApplication {

	public static void main(String[] args) {

		SpringApplication.run(BattleshipApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(GameRepository gameRepository,
                                      PlayerRepository playerRepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shipRepository) {
		return (args) -> {

            Player player1 = new Player("j.bauer@ctu.gov");
            Player player2 = new Player("c.obrian@ctu.gov");
            Player player3 = new Player("kim_bauer@gmail.com");
            Player player4 = new Player("t.almeida@ctu.gov");

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);

            Date now = new Date();

            Game game1 = new Game();
            Game game2 = new Game();
            Game game3 = new Game();
            Game game4 = new Game();
            Game game5 = new Game();
            Game game6 = new Game();
            Game game7 = new Game();
            Game game8 = new Game();

            game1.setCreationDate(now);
            game2.setCreationDate(Date.from(now.toInstant().plusSeconds(3600)));
            game3.setCreationDate(Date.from(now.toInstant().plusSeconds(3600*2)));
            game4.setCreationDate(Date.from(now.toInstant().plusSeconds(3600*3)));
            game5.setCreationDate(Date.from(now.toInstant().plusSeconds(3600*4)));
            game6.setCreationDate(Date.from(now.toInstant().plusSeconds(3600*5)));
            game7.setCreationDate(Date.from(now.toInstant().plusSeconds(3600*6)));
            game8.setCreationDate(Date.from(now.toInstant().plusSeconds(3600*7)));

            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            gameRepository.save(game4);
            gameRepository.save(game5);
            gameRepository.save(game6);
            gameRepository.save(game7);
            gameRepository.save(game8);

            GamePlayer GP11 = new GamePlayer(player1, game1);
            GamePlayer GP12 = new GamePlayer(player2, game1);
            GamePlayer GP21 = new GamePlayer(player1, game2);
            GamePlayer GP22 = new GamePlayer(player2, game2);
            GamePlayer GP31 = new GamePlayer(player2, game3);
            GamePlayer GP32 = new GamePlayer(player4, game3);
            GamePlayer GP41 = new GamePlayer(player2, game4);
            GamePlayer GP42 = new GamePlayer(player1, game4);
            GamePlayer GP51 = new GamePlayer(player4, game5);
            GamePlayer GP52 = new GamePlayer(player1, game5);
            GamePlayer GP61 = new GamePlayer(player3, game6);
            GamePlayer GP71 = new GamePlayer(player4, game7);
            GamePlayer GP81 = new GamePlayer(player3, game8);
            GamePlayer GP82 = new GamePlayer(player4, game8);

            gamePlayerRepository.save(GP11);
            gamePlayerRepository.save(GP12);
            gamePlayerRepository.save(GP21);
            gamePlayerRepository.save(GP22);
            gamePlayerRepository.save(GP31);
            gamePlayerRepository.save(GP32);
            gamePlayerRepository.save(GP41);
            gamePlayerRepository.save(GP42);
            gamePlayerRepository.save(GP51);
            gamePlayerRepository.save(GP52);
            gamePlayerRepository.save(GP61);
            gamePlayerRepository.save(GP71);
            gamePlayerRepository.save(GP81);
            gamePlayerRepository.save(GP82);

            Set<String> DS1 = new HashSet<>(Arrays.asList("H2", "H3", "H4"));
            Set<String> SB1 = new HashSet<>(Arrays.asList("E1", "F5", "G1"));
            Set<String> PB1 = new HashSet<>(Arrays.asList("B4", "B5"));
            Set<String> DS2 = new HashSet<>(Arrays.asList("B5", "C5", "D5"));
            Set<String> PB2 = new HashSet<>(Arrays.asList("F1", "F2"));
            Set<String> PB3 = new HashSet<>(Arrays.asList("C6", "C7"));
            Set<String> SB2 = new HashSet<>(Arrays.asList("A2", "A3", "A4"));
            Set<String> PB4 = new HashSet<>(Arrays.asList("G6", "H6"));

            Ship ship1 = new Ship("Destroyer", DS1, GP11);
            Ship ship2 = new Ship("Submarine", SB1, GP11);
            Ship ship3 = new Ship("Patrol Boat", PB1, GP11);
            Ship ship4 = new Ship("Destroyer", DS2, GP12);
            Ship ship5 = new Ship("Patrol Boat", PB2, GP12);
            Ship ship6 = new Ship("Destroyer", DS2, GP21);
            Ship ship7 = new Ship("Patrol Boat", PB3, GP21);
            Ship ship8 = new Ship("Submarine", SB2, GP22);
            Ship ship9 = new Ship("Patrol Boat", PB4, GP22);
            Ship ship10 = new Ship("Destroyer", DS2, GP31);
            Ship ship11 = new Ship("Patrol Boat", PB3, GP31);
            Ship ship12 = new Ship("Submarine", SB2, GP32);
            Ship ship13 = new Ship("Patrol Boat", PB4, GP32);
            Ship ship14 = new Ship("Destroyer", DS2, GP41);
            Ship ship15 = new Ship("Patrol Boat", PB3, GP41);
            Ship ship16 = new Ship("Submarine", SB2, GP42);
            Ship ship17 = new Ship("Patrol Boat", PB4, GP42);
            Ship ship18 = new Ship("Destroyer", DS2, GP51);
            Ship ship19 = new Ship("Patrol Boat", PB3, GP51);
            Ship ship20 = new Ship("Submarine", SB2, GP52);
            Ship ship21 = new Ship("Patrol Boat", PB4, GP52);
            Ship ship22 = new Ship("Destroyer", DS2, GP61);
            Ship ship23 = new Ship("Patrol Boat", PB3, GP61);
            Ship ship24 = new Ship("Destroyer", DS2,GP81);
            Ship ship25 = new Ship("Patrol Boat", PB3, GP81);
            Ship ship26 = new Ship("Submarine", SB2, GP82);
            Ship ship27 = new Ship("Patrol Boat", PB4, GP82);

            shipRepository.save(ship1);
            shipRepository.save(ship2);
            shipRepository.save(ship3);
            shipRepository.save(ship4);
            shipRepository.save(ship5);
            shipRepository.save(ship6);
            shipRepository.save(ship7);
            shipRepository.save(ship8);
            shipRepository.save(ship9);
            shipRepository.save(ship10);
            shipRepository.save(ship11);
            shipRepository.save(ship12);
            shipRepository.save(ship13);
            shipRepository.save(ship14);
            shipRepository.save(ship15);
            shipRepository.save(ship16);
            shipRepository.save(ship17);
            shipRepository.save(ship18);
            shipRepository.save(ship19);
            shipRepository.save(ship20);
            shipRepository.save(ship21);
            shipRepository.save(ship22);
            shipRepository.save(ship23);
            shipRepository.save(ship24);
            shipRepository.save(ship25);
            shipRepository.save(ship26);
            shipRepository.save(ship27);

        };
	}
}
