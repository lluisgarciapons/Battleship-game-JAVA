package com.lluis.battleship;

// import com.sun.org.apache.bcel.internal.generic.SALOAD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    public CommandLineRunner initData(GameRepository gameRepository, PlayerRepository playerRepository,
            GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository,
            ScoreRepository scoreRepository) {
        return (args) -> {

            Player player1 = new Player("Bauer24", "j.bauer@ctu.gov", "24");
            Player player2 = new Player("ZerO-brian", "c.obrian@ctu.gov", "42");
            Player player3 = new Player("Kimbi", "kim_bauer@gmail.com", "kb");
            Player player4 = new Player("xXalmeXx", "t.almeida@ctu.gov", "mole");

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
            game3.setCreationDate(Date.from(now.toInstant().plusSeconds(3600 * 2)));
            game4.setCreationDate(Date.from(now.toInstant().plusSeconds(3600 * 3)));
            game5.setCreationDate(Date.from(now.toInstant().plusSeconds(3600 * 4)));
            game6.setCreationDate(Date.from(now.toInstant().plusSeconds(3600 * 5)));
            game7.setCreationDate(Date.from(now.toInstant().plusSeconds(3600 * 6)));
            game8.setCreationDate(Date.from(now.toInstant().plusSeconds(3600 * 7)));

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
            Set<String> SB1 = new HashSet<>(Arrays.asList("E1", "F1", "G1"));
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
            Ship ship24 = new Ship("Destroyer", DS2, GP81);
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

            Set<String> s111 = new HashSet<>(Arrays.asList("B5", "C5", "F1"));
            Set<String> s112 = new HashSet<>(Arrays.asList("B4", "B5", "B6"));
            Set<String> s121 = new HashSet<>(Arrays.asList("F2", "D5"));
            Set<String> s122 = new HashSet<>(Arrays.asList("E1", "H3", "A2"));
            Set<String> s211 = new HashSet<>(Arrays.asList("A2", "A4", "G6"));
            Set<String> s212 = new HashSet<>(Arrays.asList("B5", "D5", "C7"));
            Set<String> s221 = new HashSet<>(Arrays.asList("A3", "H6"));
            Set<String> s222 = new HashSet<>(Arrays.asList("C5", "C6"));
            Set<String> s311 = new HashSet<>(Arrays.asList("G6", "H6", "A4"));
            Set<String> s312 = new HashSet<>(Arrays.asList("H1", "H2", "H3"));
            Set<String> s321 = new HashSet<>(Arrays.asList("A2", "A3", "D8"));
            Set<String> s322 = new HashSet<>(Arrays.asList("E1", "F2", "G3"));
            Set<String> s411 = new HashSet<>(Arrays.asList("A3", "A4", "F7"));
            Set<String> s412 = new HashSet<>(Arrays.asList("B5", "C6", "H1"));
            Set<String> s421 = new HashSet<>(Arrays.asList("A2", "G6", "H6"));
            Set<String> s422 = new HashSet<>(Arrays.asList("C5", "C7", "D5"));
            Set<String> s511 = new HashSet<>(Arrays.asList("A1", "A2", "A3"));
            Set<String> s512 = new HashSet<>(Arrays.asList("B5", "B6", "C7"));
            Set<String> s521 = new HashSet<>(Arrays.asList("G6", "G7", "G8"));
            Set<String> s522 = new HashSet<>(Arrays.asList("C6", "D6", "E6"));
            Set<String> s532 = new HashSet<>(Arrays.asList("H1", "H8"));

            Salvo salvo1 = new Salvo(1, s111, GP11);
            Salvo salvo2 = new Salvo(1, s112, GP12);
            Salvo salvo3 = new Salvo(2, s121, GP11);
            Salvo salvo4 = new Salvo(2, s122, GP12);
            Salvo salvo5 = new Salvo(1, s211, GP21);
            Salvo salvo6 = new Salvo(1, s212, GP22);
            Salvo salvo7 = new Salvo(2, s221, GP21);
            Salvo salvo8 = new Salvo(2, s222, GP22);
            Salvo salvo9 = new Salvo(1, s311, GP31);
            Salvo salvo10 = new Salvo(1, s312, GP32);
            Salvo salvo11 = new Salvo(2, s321, GP31);
            Salvo salvo12 = new Salvo(2, s322, GP32);
            Salvo salvo13 = new Salvo(1, s411, GP41);
            Salvo salvo14 = new Salvo(1, s412, GP42);
            Salvo salvo15 = new Salvo(2, s421, GP41);
            Salvo salvo16 = new Salvo(2, s422, GP42);
            Salvo salvo17 = new Salvo(1, s511, GP51);
            Salvo salvo18 = new Salvo(1, s512, GP52);
            Salvo salvo19 = new Salvo(2, s521, GP51);
            Salvo salvo20 = new Salvo(2, s522, GP52);
            Salvo salvo21 = new Salvo(3, s532, GP52);

            salvoRepository.save(salvo1);
            salvoRepository.save(salvo2);
            salvoRepository.save(salvo3);
            salvoRepository.save(salvo4);
            salvoRepository.save(salvo5);
            salvoRepository.save(salvo6);
            salvoRepository.save(salvo7);
            salvoRepository.save(salvo8);
            salvoRepository.save(salvo9);
            salvoRepository.save(salvo10);
            salvoRepository.save(salvo11);
            salvoRepository.save(salvo12);
            salvoRepository.save(salvo13);
            salvoRepository.save(salvo14);
            salvoRepository.save(salvo15);
            salvoRepository.save(salvo16);
            salvoRepository.save(salvo17);
            salvoRepository.save(salvo18);
            salvoRepository.save(salvo19);
            salvoRepository.save(salvo20);
            salvoRepository.save(salvo21);

            Score score11 = new Score(game1, player1, 1.0);
            Score score12 = new Score(game1, player2, 0.0);
            Score score21 = new Score(game2, player1, 0.5);
            Score score22 = new Score(game2, player2, 0.5);
            Score score31 = new Score(game3, player2, 1.0);
            Score score32 = new Score(game3, player4, 0.0);
            Score score41 = new Score(game4, player2, 0.5);
            Score score42 = new Score(game4, player1, 0.5);

            scoreRepository.save(score11);
            scoreRepository.save(score12);
            scoreRepository.save(score21);
            scoreRepository.save(score22);
            scoreRepository.save(score31);
            scoreRepository.save(score32);
            scoreRepository.save(score41);
            scoreRepository.save(score42);

        };
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName -> {
            Player player = playerRepository.findByEmail(inputName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(), AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/web/**").permitAll().antMatchers("/favicon.ico").permitAll()
                .antMatchers("/api/games").permitAll().antMatchers("/api/players").permitAll().anyRequest()
                .fullyAuthenticated();

        http.formLogin().usernameParameter("email").passwordParameter("password").loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling()
                .authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

}