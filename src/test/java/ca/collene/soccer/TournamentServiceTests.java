package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIn.in;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import ca.collene.soccer.entities.Game;
import ca.collene.soccer.entities.Team;
import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.repositories.TournamentRepository;
import ca.collene.soccer.services.GameAlreadyInTournamentException;
import ca.collene.soccer.services.GameDoesNotExistException;
import ca.collene.soccer.services.InvalidGameException;
import ca.collene.soccer.services.InvalidScoreException;
import ca.collene.soccer.services.NameAlreadyExistsException;
import ca.collene.soccer.services.TeamAlreadyInTournamentException;
import ca.collene.soccer.services.TeamDoesNotExistException;
import ca.collene.soccer.services.TeamNotInGameException;
import ca.collene.soccer.services.TeamService;
import ca.collene.soccer.services.TournamentDoesNotExistException;
import ca.collene.soccer.services.TournamentService;

@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class TournamentServiceTests {
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TournamentRepository tournamentRepository;
    
    @Test
    public void create_tournament_works() throws Exception {
        final String testTournamentName = "Test tournament";
        assertThat(tournamentRepository.count(), is(equalTo(0l)));        
        assertThrows(TournamentDoesNotExistException.class, () -> {
            tournamentService.getTournament(testTournamentName);
        });
        Tournament newTournament = tournamentService.createTournament(testTournamentName);
        assertThat(tournamentService.getTournament(testTournamentName), is(equalTo(newTournament)));
        assertThat(tournamentRepository.count(), is(equalTo(1l)));
    }

    @Test
    public void create_tournament_with_duplicate_name_fails() throws Exception {
        final String sameTournamentName = "Test tournament";
        tournamentService.createTournament(sameTournamentName);        
        assertThat(tournamentRepository.count(), is(equalTo(1l)));

        // make sure the exception is thrown
        assertThrows(NameAlreadyExistsException.class, () -> {
            tournamentService.createTournament(sameTournamentName);
        });

        // make sure that a second tournament hasn't been added after the exception was thrown
        assertThat(tournamentRepository.count(), is(equalTo(1l)));
    }

    @Test
    public void get_tournament_by_name_works() throws Exception {    
        final String tournamentName = "Test tournament";
        Tournament newTournament = tournamentService.createTournament(tournamentName);            
        assertThat(tournamentService.getTournament(tournamentName), is(equalTo(newTournament)));
    }
    
    @Test
    public void get_tournament_by_name_that_does_not_exist_fails() {
        final String tournamentName = "Tournament that does not exist";
        assertThrows(TournamentDoesNotExistException.class, () -> {
            tournamentService.getTournament(tournamentName);
        });
    }
    
    // adding team that exists, is not in tournament yet
    @Test
    public void add_team_that_exists_adds_team_to_tournament_works() throws Exception {
        final String tournamentName = "Tournament";
        final String teamName = "Team";
        Tournament tournament = tournamentService.createTournament(tournamentName);
        Team team = teamService.createTeam(teamName);

        // make sure we start with an empty list of teams when a new tournament is created
        assertThat(tournament.getTeams(), is(empty()));
        tournamentService.addTeamToTournament(teamName, tournament);        
        assertThat(tournament.getTeams(), hasSize(1));
        assertThat(team, is(in(tournament.getTeams())));
        
        // make sure that the team was actually created
        assertThat(teamService.getTeam(teamName), is(equalTo(team)));
    }

    @Test
    public void add_multiple_teams_to_tournament_works() throws Exception {
        final String tournamentName = "Tournament";
        final String teamName1 = "Team1";
        final String teamName2 = "Team2";
        Tournament tournament = tournamentService.createTournament(tournamentName);
        Team team1 = teamService.createTeam(teamName1);
        
        // make sure we start with an empty list of teams
        assertThat(tournament.getTeams(), is(empty()));

        tournamentService.addTeamToTournament(teamName1, tournament);
        tournamentService.addTeamToTournament(teamName2, tournament);

        assertThat(tournament.getTeams(), hasSize(2));
        assertThat(team1, is(in(tournament.getTeams())));
        assertThat(teamService.getTeam(teamName2), is(in(tournament.getTeams())));
    }

    // adding team that exists, but is already in tournament
    @Test
    public void add_team_already_in_tournament_fails() throws Exception {
        final String tournamentName = "Tournament";
        final String teamName = "Team";
        Tournament tournament = tournamentService.createTournament(tournamentName);
        Team team = teamService.createTeam(teamName);
        tournamentService.addTeamToTournament(teamName, tournament);

        // make sure that the team is already in the tournament        
        assertThat(team, is(in(tournament.getTeams())));

        assertThrows(TeamAlreadyInTournamentException.class, () -> {
            tournamentService.addTeamToTournament(teamName, tournament);
        });
    }
    
    // adding team that does not exist creates the team and adds them to the tournament
    @Test
    public void add_team_that_does_not_exist_creates_team_and_adds_to_tournament() throws Exception {
        final String tournamentName = "Tournament";
        final String teamName = "Team";
        Tournament tournament = tournamentService.createTournament(tournamentName);
        // make sure that the team doesn't exist
        assertThrows(TeamDoesNotExistException.class, () -> {
            teamService.getTeam(teamName);
        });

        tournamentService.addTeamToTournament(teamName, tournament);

        Team team = teamService.getTeam(teamName);        
        assertThat(team, is(in(tournament.getTeams())));
    }

    // adding game works
    @Test
    public void add_game_between_teams_that_exist_and_in_tournament_already_works() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        Tournament tournament = tournamentService.createTournament(tournamentName);
        Team team1 = teamService.createTeam(team1Name);
        Team team2 = teamService.createTeam(team2Name);

        tournamentService.addTeamToTournament(team1Name, tournament);
        tournamentService.addTeamToTournament(team2Name, tournament);
                
        assertThat(tournament.getGames(), hasSize(0));

        tournamentService.addGameToTournament(team1Name, team2Name, tournament);
        assertThat(tournament.getGames(), hasSize(1));

        Game game = tournament.getGames().get(0);
        assertThat(team1, is(in(game.getTeams())));
        assertThat(team2, is(in(game.getTeams())));
    }

    // adding team that exists but isn't in tournament to a game also adds them to the tournament
    @Test
    public void add_game_between_teams_that_exist_but_not_in_tournament_adds_them_to_tournament() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        Tournament tournament = tournamentService.createTournament(tournamentName);
        Team team1 = teamService.createTeam(team1Name);
        Team team2 = teamService.createTeam(team2Name);
        
        assertThat(tournament.getTeams(), hasSize(0));
        assertThat(tournament.getGames(), hasSize(0));

        tournamentService.addGameToTournament(team1Name, team2Name, tournament);        
        assertThat(tournament.getTeams(), hasSize(2));
        assertThat(tournament.getGames(), hasSize(1));
        Game game = tournament.getGames().get(0);
        assertThat(team1, is(in(game.getTeams())));
        assertThat(team2, is(in(game.getTeams())));
    }

    // adding team that doesn't exist to a game creates them and also adds them to the tournament
    @Test
    public void add_game_when_teams_do_not_exist_creates_them_and_adds_them_to_tournament() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        Tournament tournament = tournamentService.createTournament(tournamentName);
        assertThat(tournament.getTeams(), hasSize(0));
        assertThat(tournament.getGames(), hasSize(0));
        // make sure that the teams don't exist
        assertThrows(TeamDoesNotExistException.class, () -> {
            teamService.getTeam(team1Name);
        });
        assertThrows(TeamDoesNotExistException.class, () -> {
            teamService.getTeam(team2Name);
        });

        tournamentService.addGameToTournament(team1Name, team2Name, tournament);
        assertThat(tournament.getTeams(), hasSize(2));
        assertThat(tournament.getGames(), hasSize(1));
        Game game = tournament.getGames().get(0);
        assertThat(teamService.getTeam(team1Name), is(in(game.getTeams())));
        assertThat(teamService.getTeam(team2Name), is(in(game.getTeams())));
    }

    // adding a game to a tournament that already has these teams fails
    @Test
    public void add_game_that_already_exists_in_tournament_fails() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        Tournament tournament = tournamentService.createTournament(tournamentName);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament);
        assertThrows(GameAlreadyInTournamentException.class, () -> {
            tournamentService.addGameToTournament(team1Name, team2Name, tournament);
        });
        assertThat(tournament.getGames(), hasSize(1));
    }

    // adding a game to a tournament that already has these teams but added in opposite order still fails
    @Test
    public void add_game_that_already_exists_in_tournament_in_opposite_team_order_fails() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        Tournament tournament = tournamentService.createTournament(tournamentName);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament);
        assertThrows(GameAlreadyInTournamentException.class, () -> {
            tournamentService.addGameToTournament(team2Name, team1Name, tournament);
        });
        assertThat(tournament.getGames(), hasSize(1));
    }
    
    // a team can not play itself
    @Test
    public void add_game_with_same_team_fails() throws Exception {
        final String tournamentName = "Tournament";
        final String teamName = "One and Only Team";
        Tournament tournament = tournamentService.createTournament(tournamentName);
        assertThrows(InvalidGameException.class, () -> {
            tournamentService.addGameToTournament(teamName, teamName, tournament);
        });
        assertThat(tournament.getGames(), is(empty()));
    }

    // adding multiple games to a tournament works
    @Test
    public void add_multiple_games_to_a_tournament_works() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team Three";
        final String team4Name = "Team Four";

        Tournament tournament = tournamentService.createTournament(tournamentName);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament);
        tournamentService.addGameToTournament(team1Name, team3Name, tournament);
        tournamentService.addGameToTournament(team1Name, team4Name, tournament);
        tournamentService.addGameToTournament(team2Name, team3Name, tournament);
        tournamentService.addGameToTournament(team2Name, team4Name, tournament);
        tournamentService.addGameToTournament(team3Name, team4Name, tournament);

        assertThat(tournament.getGames(), hasSize(6));
        assertThat(tournament.getTeams(), hasSize(4));
    }

    // adding multiple games to multiple tournaments works
    @Test
    public void add_multiple_games_to_multiple_tournaments_works() throws Exception {
        final String tournament1Name = "Tournament One";
        final String tournament2Name = "Tournament Two";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team Three";
        final String team4Name = "Team Four";

        Tournament tournament1 = tournamentService.createTournament(tournament1Name);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament1);
        tournamentService.addGameToTournament(team1Name, team3Name, tournament1);        

        assertThat(tournament1.getGames(), hasSize(2));
        assertThat(tournament1.getTeams(), hasSize(3));

        Tournament tournament2 = tournamentService.createTournament(tournament2Name);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament2);        
        tournamentService.addGameToTournament(team1Name, team4Name, tournament2);
        tournamentService.addGameToTournament(team2Name, team3Name, tournament2);
        tournamentService.addGameToTournament(team2Name, team4Name, tournament2);
        tournamentService.addGameToTournament(team3Name, team4Name, tournament2);

        assertThat(tournament2.getGames(), hasSize(5));
        assertThat(tournament2.getTeams(), hasSize(4));
    }

    // adding score to game works
    @Test
    public void score_game_works() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final int team1Points = 1;
        final int team2Points = 2;        

        Tournament tournament = tournamentService.createTournament(tournamentName);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament);

        assertThat(tournament.getGames(), hasSize(1));
        assertFalse(tournament.getGames().get(0).hasScore());

        tournamentService.scoreGameInTournament(team1Name, team1Points, team2Name, team2Points, tournament);
        Game game = tournament.getGames().get(0);
        assertTrue(game.hasScore());
        Team team1 = teamService.getTeam(team1Name);
        Team team2 = teamService.getTeam(team2Name);
        assertThat(game.getPointsForTeam(team1), is(equalTo(team1Points)));
        assertThat(game.getPointsForTeam(team2), is(equalTo(team2Points)));
    }

    @Test
    public void score_game_with_opposite_order_as_added_to_tournament_still_works() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final int team1Points = 1;
        final int team2Points = 2;        

        Tournament tournament = tournamentService.createTournament(tournamentName);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament);

        assertThat(tournament.getGames(), hasSize(1));
        assertFalse(tournament.getGames().get(0).hasScore());

        tournamentService.scoreGameInTournament(team2Name, team2Points, team1Name, team1Points, tournament);
        Game game = tournament.getGames().get(0);
        assertTrue(game.hasScore());
        Team team1 = teamService.getTeam(team1Name);
        Team team2 = teamService.getTeam(team2Name);
        assertThat(game.getPointsForTeam(team1), is(equalTo(team1Points)));
        assertThat(game.getPointsForTeam(team2), is(equalTo(team2Points)));
    }

    // adding score to a game with a wrong team fails (game doesn't exist)
    @Test
    public void score_game_with_wrong_team_fails() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team not in game";
        final int team1Points = 1;
        final int team2Points = 2;

        Tournament tournament = tournamentService.createTournament(tournamentName);
        tournamentService.addTeamToTournament(team1Name, tournament);
        tournamentService.addTeamToTournament(team2Name, tournament);
        tournamentService.addTeamToTournament(team3Name, tournament);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament);
        
        assertThrows(GameDoesNotExistException.class, () -> {
            tournamentService.scoreGameInTournament(team1Name, team1Points, team3Name, team2Points, tournament);            
        });
        Game game = tournament.getGames().get(0);
        assertFalse(game.hasScore());
    }

    // getting score for a game for a wrong team fails
    @Test
    public void get_score_with_wrong_team_fails() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team not in game";
        final int team1Points = 1;
        final int team2Points = 2;

        Tournament tournament = tournamentService.createTournament(tournamentName);
        tournamentService.addTeamToTournament(team1Name, tournament);
        tournamentService.addTeamToTournament(team2Name, tournament);
        tournamentService.addTeamToTournament(team3Name, tournament);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament);

        tournamentService.scoreGameInTournament(team1Name, team1Points, team2Name, team2Points, tournament);
        Game game = tournament.getGames().get(0);
        Team team3 = teamService.getTeam(team3Name);
        
        assertThrows(TeamNotInGameException.class, () -> {
            game.getPointsForTeam(team3);
        });        
    }

    // adding negative points fails
    @Test
    public void setting_negative_points_in_score_fails_and_score_not_saved() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final int team1Points = 1;
        final int team2Points = -2;        

        Tournament tournament = tournamentService.createTournament(tournamentName);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament);

        assertThat(tournament.getGames(), hasSize(1));
        assertFalse(tournament.getGames().get(0).hasScore());

        assertThrows(InvalidScoreException.class, () -> {
            tournamentService.scoreGameInTournament(team1Name, team1Points, team2Name, team2Points, tournament);
        });

        assertFalse(tournament.getGames().get(0).hasScore());
    }

    // adding tied score works
    @Test
    public void setting_tied_score_works() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final int teamPoints = 1;        

        Tournament tournament = tournamentService.createTournament(tournamentName);
        tournamentService.addGameToTournament(team1Name, team2Name, tournament);

        assertThat(tournament.getGames(), hasSize(1));
        assertFalse(tournament.getGames().get(0).hasScore());

        tournamentService.scoreGameInTournament(team1Name, teamPoints, team2Name, teamPoints, tournament);
        Game game = tournament.getGames().get(0);
        assertTrue(game.hasScore());
        Team team1 = teamService.getTeam(team1Name);
        Team team2 = teamService.getTeam(team2Name);
        assertThat(game.getPointsForTeam(team1), is(equalTo(teamPoints)));
        assertThat(game.getPointsForTeam(team2), is(equalTo(teamPoints)));
    }

    // setting score for a game that hasn't been added fails and doesn't add the teams to a tournament
    @Test
    public void setting_score_for_game_that_does_not_exist_where_teams_also_not_in_tournament_fails_and_does_not_add_teams_to_tournament() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";        
        final int team1Points = 1;
        final int team2Points = 2;

        Tournament tournament = tournamentService.createTournament(tournamentName);
        teamService.createTeam(team1Name);
        teamService.createTeam(team2Name);
        
        assertThat(tournament.getTeams(), is(empty()));
        assertThat(tournament.getGames(), hasSize(0));

        assertThrows(GameDoesNotExistException.class, () -> {
            tournamentService.scoreGameInTournament(team1Name, team1Points, team2Name, team2Points, tournament);
        });

        assertThat(tournament.getTeams(), is(empty()));
    }

    // setting score for a game where a team doesn't exist fails and doesn't add the team to the tournament nor the team service
    @Test
    public void setting_score_for_game_where_team_does_not_exist_fails_and_does_not_add_team_to_tournament_nor_team_service() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";        
        final int team1Points = 1;
        final int team2Points = 2;

        Tournament tournament = tournamentService.createTournament(tournamentName);        
        teamService.createTeam(team2Name);
        
        assertThat(tournament.getTeams(), is(empty()));
        assertThat(tournament.getGames(), hasSize(0));

        assertThrows(TeamDoesNotExistException.class, () -> {
            tournamentService.scoreGameInTournament(team1Name, team1Points, team2Name, team2Points, tournament);
        });

        assertThat(tournament.getTeams(), is(empty()));
        assertThrows(TeamDoesNotExistException.class, () -> {
            teamService.getTeam(team1Name);
        });
    }
}
