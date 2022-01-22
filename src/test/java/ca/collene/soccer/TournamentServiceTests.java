package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIn.in;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import ca.collene.soccer.entities.Team;
import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.repositories.TournamentRepository;
import ca.collene.soccer.services.NameAlreadyExistsException;
import ca.collene.soccer.services.TeamAlreadyInTournamentException;
import ca.collene.soccer.services.TeamDoesNotExistException;
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

        tournamentService.addTeamToTournament(teamName, tournamentName);

        // since we changed the tournament from above, we need to load it again from the service to get the added team
        tournament = tournamentService.getTournament(tournamentName);
        assertThat(tournament.getTeams(), hasSize(1));
        assertThat(team, is(in(tournament.getTeams())));
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

        tournamentService.addTeamToTournament(teamName1, tournamentName);
        tournamentService.addTeamToTournament(teamName2, tournamentName);

        tournament = tournamentService.getTournament(tournamentName);
        assertThat(tournament.getTeams(), hasSize(2));
        assertThat(team1, is(in(tournament.getTeams())));
        assertThat(teamService.getTeam(teamName2), is(in(tournament.getTeams())));
    }

    // adding team that exists, but is already in tournament
    @Test
    public void add_team_already_in_tournament_fails() throws Exception {
        final String tournamentName = "Tournament";
        final String teamName = "Team";
        tournamentService.createTournament(tournamentName);
        Team team = teamService.createTeam(teamName);
        tournamentService.addTeamToTournament(teamName, tournamentName);
        // make sure that the team is already in the tournament
        Tournament tournament = tournamentService.getTournament(tournamentName);
        assertThat(team, is(in(tournament.getTeams())));

        assertThrows(TeamAlreadyInTournamentException.class, () -> {
            tournamentService.addTeamToTournament(teamName, tournamentName);
        });
    }
    
    // adding team that does not exist creates the team and adds them to the tournament
    @Test
    public void add_team_that_does_not_exist_creates_team_and_adds_to_tournament() throws Exception {
        final String tournamentName = "Tournament";
        final String teamName = "Team";
        tournamentService.createTournament(tournamentName);
        // make sure that the team doesn't exist
        assertThrows(TeamDoesNotExistException.class, () -> {
            teamService.getTeam(teamName);
        });

        tournamentService.addTeamToTournament(teamName, tournamentName);

        Team team = teamService.getTeam(teamName);        
        Tournament tournament = tournamentService.getTournament(tournamentName);
        assertThat(team, is(in(tournament.getTeams())));
    }
}
