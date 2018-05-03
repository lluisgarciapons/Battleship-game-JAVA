package com.lluis.battleship;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class BattleshipApplication {

	public static void main(String[] args) {

		SpringApplication.run(BattleshipApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(GameRepository gameRepository, PlayerRepository playerRepository, GamePlayerRepository gamePlayerRepository) {
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

            GamePlayer GP1 = new GamePlayer(player1, game1);
            GamePlayer GP2 = new GamePlayer(player2, game2);
            GamePlayer GP3 = new GamePlayer(player3, game3);
            GamePlayer GP4 = new GamePlayer(player4, game4);
            GamePlayer GP5 = new GamePlayer(player1, game5);
            GamePlayer GP6 = new GamePlayer(player2, game6);
            GamePlayer GP7 = new GamePlayer(player3, game7);
            GamePlayer GP8 = new GamePlayer(player4, game8);

            gamePlayerRepository.save(GP1);
            gamePlayerRepository.save(GP2);
            gamePlayerRepository.save(GP3);
            gamePlayerRepository.save(GP4);
            gamePlayerRepository.save(GP5);
            gamePlayerRepository.save(GP6);
            gamePlayerRepository.save(GP7);
            gamePlayerRepository.save(GP8);

		};
	}
}
