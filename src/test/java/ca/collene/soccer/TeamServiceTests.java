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

import ca.collene.soccer.entities.Person;
import ca.collene.soccer.entities.Player;
import ca.collene.soccer.entities.Team;
import ca.collene.soccer.repositories.TeamRepository;
import ca.collene.soccer.services.CoachAlreadyOnTeamException;
import ca.collene.soccer.services.NameAlreadyExistsException;
import ca.collene.soccer.services.NumberAlreadyInUseException;
import ca.collene.soccer.services.PersonDoesNotExistException;
import ca.collene.soccer.services.PersonService;
import ca.collene.soccer.services.PlayerAlreadyOnTeamException;
import ca.collene.soccer.services.TeamDoesNotExistException;
import ca.collene.soccer.services.TeamService;

@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class TeamServiceTests {
    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonService personService;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    public void create_team_works() throws Exception {
        final String testTeamName = "Test team";
        assertThat(teamRepository.count(), is(equalTo(0l)));        
        assertThrows(TeamDoesNotExistException.class, () -> {
            teamService.getTeam(testTeamName);
        });
        Team newTeam = teamService.createTeam(testTeamName);
        assertThat(teamService.getTeam(testTeamName), is(equalTo(newTeam)));
        assertThat(teamRepository.count(), is(equalTo(1l)));
    }

    @Test
    public void create_team_with_duplicate_name_fails() throws Exception {
        final String sameTeamName = "Test team";
        teamService.createTeam(sameTeamName);        
        assertThat(teamRepository.count(), is(equalTo(1l)));

        // make sure the exception is thrown
        assertThrows(NameAlreadyExistsException.class, () -> {
            teamService.createTeam(sameTeamName);
        });

        // make sure that a second team hasn't been added after the exception was thrown
        assertThat(teamRepository.count(), is(equalTo(1l)));
    }

    @Test
    public void get_team_by_name_works() throws Exception {    
        final String testTeamName = "Test team";
        Team newTeam = teamService.createTeam(testTeamName);            
        assertThat(teamService.getTeam(testTeamName), is(equalTo(newTeam)));
    }

    @Test
    public void get_team_by_name_that_does_not_exist_fails() {
        final String testTeamName = "Team that does not exist";
        assertThrows(TeamDoesNotExistException.class, () -> {
            teamService.getTeam(testTeamName);
        });
    }

    // adding a coach who exists to a team that exists
    @Test
    public void add_coach_who_exists_to_a_team_that_exists_adds_coach() throws Exception {
        final String teamName = "Test team";
        final String coachName = "Coach Smith";
        Team team = teamService.createTeam(teamName);
        Person coach = personService.createPerson(coachName);

        // make sure the team doesn't have a coach before adding one
        assertThat(team.getCoaches(), is(empty()));

        teamService.addCoachToTeam(coachName, teamName);
        // since we changed the team, we need to load it again from the service
        team = teamService.getTeam(teamName);
        assertThat(coach, is(in(team.getCoaches())));
    }

    // adding a coach who exists to a team that doesn't exist
    @Test
    public void add_coach_who_exists_to_team_that_does_not_creates_team_and_adds_coach() throws Exception {
        final String teamName = "Test team";
        final String coachName = "Coach Smith";
        Person coach = personService.createPerson(coachName);

        // make sure that the team doesn't exist        
        assertThrows(TeamDoesNotExistException.class, () -> {
            teamService.getTeam(teamName);
        });
        
        teamService.addCoachToTeam(coachName, teamName);
        Team team = teamService.getTeam(teamName);
        assertThat(coach, is(in(team.getCoaches())));
    }

    // adding a coach who doesn't exist to a team that exists
    @Test
    public void add_coach_who_does_not_exist_to_a_team_that_does_creates_person_and_adds_coach() throws Exception {
        final String teamName = "Test team";
        final String coachName = "Coach Smith";
        Team team = teamService.createTeam(teamName);

        // make sure that the person doesn't exist
        assertThrows(PersonDoesNotExistException.class, () -> {
            personService.getPerson(coachName);
        });

        teamService.addCoachToTeam(coachName, teamName);
        Person coach = personService.getPerson(coachName);
        team = teamService.getTeam(teamName);
        assertThat(coach, is(in(team.getCoaches())));
    }

    // adding a coach who doesn't exist to a team that doesn't exist
    @Test
    public void add_coach_who_does_not_exist_to_a_team_that_does_not_exists_creates_both_and_adds_coach() throws Exception {
        final String teamName = "Test team";
        final String coachName = "Coach Smith";

        // make sure that the person doesn't exist
        assertThrows(PersonDoesNotExistException.class, () -> {
            personService.getPerson(coachName);
        });
        // make sure that the team doesn't exist
        assertThrows(TeamDoesNotExistException.class, () -> {
            teamService.getTeam(teamName);
        });

        teamService.addCoachToTeam(coachName, teamName);
        Person coach = personService.getPerson(coachName);
        Team team = teamService.getTeam(teamName);
        assertThat(coach, is(in(team.getCoaches())));
    }
    
    @Test
    public void add_coach_twice_fails() throws Exception {
        final String teamName = "Test team";
        final String coachName = "Coach Smith";

        teamService.addCoachToTeam(coachName, teamName);
        assertThrows(CoachAlreadyOnTeamException.class, () -> {
            teamService.addCoachToTeam(coachName, teamName);
        });
        Person coach = personService.getPerson(coachName);
        Team team = teamService.getTeam(teamName);
        assertThat(coach, is(in(team.getCoaches())));
    }
    
    @Test
    public void adding_multiple_coaches_works() throws Exception {
        final String teamName = "Test team";
        final String coachName1 = "Coach One";
        final String coachName2 = "Coach Two";

        teamService.addCoachToTeam(coachName1, teamName);
        teamService.addCoachToTeam(coachName2, teamName);
        Team team = teamService.getTeam(teamName);
        assertThat(team.getCoaches(), hasSize(2));
        assertThat(personService.getPerson(coachName1), is(in(team.getCoaches())));
        assertThat(personService.getPerson(coachName2), is(in(team.getCoaches())));
    }

    @Test
    public void adding_multiple_coaches_to_multiple_teams_works() throws Exception {
        final String teamName1 = "Team One";
        final String teamName2 = "Team Two";
        final String coachName1 = "Coach One";
        final String coachName2 = "Coach Two";
        final String coachName3 = "Coach Three";
        
        teamService.addCoachToTeam(coachName1, teamName1);
        teamService.addCoachToTeam(coachName2, teamName1);
        teamService.addCoachToTeam(coachName3, teamName2);

        Team team1 = teamService.getTeam(teamName1);
        Team team2 = teamService.getTeam(teamName2);
        assertThat(team1.getCoaches(), hasSize(2));
        assertThat(team2.getCoaches(), hasSize(1));
        assertThat(personService.getPerson(coachName1), is(in(team1.getCoaches())));
        assertThat(personService.getPerson(coachName2), is(in(team1.getCoaches())));
        assertThat(personService.getPerson(coachName3), is(in(team2.getCoaches())));
    }

    @Test
    public void get_or_create_team_that_exists_returns_team() throws Exception {
        final String teamName = "Team";
        Team newTeam = teamService.createTeam(teamName);
        
        Team queriedTeam = teamService.getOrCreateTeam(teamName);
        assertThat(teamRepository.count(), is(equalTo(1l)));
        assertThat(queriedTeam, is(equalTo(newTeam)));
    }

    @Test
    public void get_or_create_team_that_does_not_exist_creates_team() throws Exception {
        final String teamName = "Team";
        
        assertThat(teamRepository.count(), is(equalTo(0l)));
        Team newTeam = teamService.createTeam(teamName);
        assertThat(teamRepository.count(), is(equalTo(1l)));
        assertThat(teamService.getTeam(teamName), is(equalTo(newTeam)));
    }  

    @Test
    public void add_player_works() throws Exception {
        final String teamName = "Team";
        final String personName = "Jane Doe";
        final int playerNumber = 10;
        Team team = teamService.createTeam(teamName);
        Person person = personService.createPerson(personName);

        teamService.addPlayerToTeam(personName, teamName, playerNumber);

        // need to load team again from the service
        team = teamService.getTeam(teamName);
        assertThat(team.getPlayers(), hasSize(1));        
        Player player = team.getPlayers().get(0);
        assertThat(player.getPerson(), is(equalTo(person)));
        assertThat(player.getNumber(), is(equalTo(playerNumber)));
        assertThat(player.getTeam(), is(equalTo(team)));
    }

    @Test
    public void add_player_who_does_not_exist_works() throws Exception {
        final String teamName = "team";
        final String personName = "Jane Doe";
        final int playerNumber = 10;

        // make sure that the person doesn't exist
        assertThrows(PersonDoesNotExistException.class, () -> {
            personService.getPerson(personName);
        });        

        teamService.addPlayerToTeam(personName, teamName, playerNumber);

        Team team = teamService.getTeam(teamName);
        assertThat(team.getPlayers(), hasSize(1));        
        Player player = team.getPlayers().get(0);
        assertThat(player.getPerson(), is(equalTo(personService.getPerson(personName))));
        assertThat(player.getNumber(), is(equalTo(playerNumber)));
        assertThat(player.getTeam(), is(equalTo(team)));
    }

    @Test
    public void add_multiple_players_to_same_team_works() throws Exception {
        final String teamName = "Team";
        final String person1Name = "Person One";
        final String person2Name = "Person Two";
        final int player1Number = 10;
        final int player2Number = 20;

        assertThat(teamRepository.count(), is(equalTo(0l)));

        teamService.addPlayerToTeam(person1Name, teamName, player1Number);
        teamService.addPlayerToTeam(person2Name, teamName, player2Number);

        Team team = teamService.getTeam(teamName);
        assertThat(team.getPlayers(), hasSize(2));
        assertThat(new Player.With().person(person1Name)
                                    .number(player1Number)
                                    .team(teamName)
                                .build(),
                    is(in(team.getPlayers())));
        assertThat(new Player.With().person(person2Name)
                                    .number(player2Number)
                                    .team(teamName)
                                .build(),
                    is(in(team.getPlayers())));        
    }

    @Test
    public void add_multiple_players_to_multiple_teams_works() throws Exception {
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String person1Name = "Person One";
        final String person2Name = "Person Two";
        final String person3Name = "Person Three";
        final int player1Number = 10;
        final int player2Number = 20;
        final int player3Number = 30;

        teamService.addPlayerToTeam(person1Name, team1Name, player1Number);
        teamService.addPlayerToTeam(person2Name, team1Name, player2Number);
        teamService.addPlayerToTeam(person3Name, team2Name, player3Number);

        Team team1 = teamService.getTeam(team1Name);
        assertThat(team1.getPlayers(), hasSize(2));
        assertThat(new Player.With().person(person1Name)
                                    .number(player1Number)
                                    .team(team1Name)
                                .build(),
                    is(in(team1.getPlayers())));
        assertThat(new Player.With().person(person2Name)
                                    .number(player2Number)
                                    .team(team1Name)
                                .build(),
                    is(in(team1.getPlayers())));

        Team team2 = teamService.getTeam(team2Name);
        assertThat(team2.getPlayers(), hasSize(1));
        assertThat(new Player.With().person(person3Name)
                                    .number(player3Number)
                                    .team(team2Name)
                                .build(),
                    is(in(team2.getPlayers())));
    }

    // adding same person with different number fails
    @Test
    public void add_same_person_to_team_with_different_number_fails() throws Exception {
        final String personName = "Jane Doe";
        final String teamName = "Team";
        final int playerNumber1 = 10;
        final int playerNumber2 = 20;

        teamService.addPlayerToTeam(personName, teamName, playerNumber1);
        assertThrows(PlayerAlreadyOnTeamException.class, () -> {
            teamService.addPlayerToTeam(personName, teamName, playerNumber2);
        });
        Team team = teamService.getTeam(teamName);
        assertThat(team.getPlayers(), hasSize(1));   
        assertThat(new Player.With().person(personName)
                                    .number(playerNumber1)
                                    .team(teamName)
                                .build(),
                    is(in(team.getPlayers())));
    }

    // adding same person with same number fails
    @Test
    public void add_same_person_to_team_with_same_number_fails() throws Exception {
        final String personName = "Jane Doe";
        final String teamName = "Team";
        final int playerNumber = 10;        

        teamService.addPlayerToTeam(personName, teamName, playerNumber);
        assertThrows(PlayerAlreadyOnTeamException.class, () -> {
            teamService.addPlayerToTeam(personName, teamName, playerNumber);
        });
        Team team = teamService.getTeam(teamName);
        assertThat(team.getPlayers(), hasSize(1));   
        assertThat(new Player.With().person(personName)
                                    .number(playerNumber)
                                    .team(teamName)
                                .build(),
                    is(in(team.getPlayers())));
    }

    // adding different person with same number as another player fails
    @Test
    public void add_different_person_to_team_with_same_number_as_another_player_fails() throws Exception {
        final String person1Name = "Person One";
        final String person2Name = "Person Two";
        final String teamName = "Team";
        final int playerNumber = 10;

        teamService.addPlayerToTeam(person1Name, teamName, playerNumber);
        assertThrows(NumberAlreadyInUseException.class, () -> {
            teamService.addPlayerToTeam(person2Name, teamName, playerNumber);
        });
        Team team = teamService.getTeam(teamName);
        assertThat(team.getPlayers(), hasSize(1));        
    }
}
