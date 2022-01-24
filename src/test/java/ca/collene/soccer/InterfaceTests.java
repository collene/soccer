package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;

import java.io.IOException;
import java.io.StringReader;

import org.jline.reader.Parser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Input;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.FileInputProvider;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.shell.result.DefaultResultHandler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class InterfaceTests {
    @Autowired
	private Shell shell;

    @Autowired
    private Parser parser;

    @Autowired
    private DefaultResultHandler resultHandler;

    @Test
    public void create_tournament_command_works() {
        final String tournamentName = "Tournament";
        Object createTournament = executeCommandInShell("create-tournament " + tournamentName);
		assertThat(createTournament, is("Tournament with name '" + tournamentName + "' created"));
    }

    @Test
    public void create_tournament_with_spaces_and_quotes_works() {
        final String tournamentName = "Test Tournament with \"spaces\"";
        final String stringCommand = String.format("create-tournament '%s'", tournamentName);
        Object createTournament = executeCommandInShell(stringCommand);        
        assertThat(createTournament, is("Tournament with name '" + tournamentName + "' created"));
    }

    @Test
    public void create_tournament_already_added_displays_error() {
        final String tournamentName = "SameName";
        final String command = "create-tournament " + tournamentName;
        Object firstTournament = executeCommandInShell(command);
        assertThat(firstTournament, is("Tournament with name '" + tournamentName + "' created"));
        Object secondTournament = executeCommandInShell(command);
        assertThat(secondTournament, is("Tournament with name '" + tournamentName + "' already exists"));
    }

    @Test
    public void create_team_command_works() {
        final String teamName = "Team";        
        Object createTeam = executeCommandInShell("create-team " + teamName);
        assertThat(createTeam, is("Team with name '" + teamName + "' created"));
    }

    @Test
    public void create_team_already_added_displays_error() {
        final String teamName = "SameName";
        final String command = "create-team " + teamName;
        Object firstTeam = executeCommandInShell(command);
        assertThat(firstTeam, is("Team with name '" + teamName + "' created"));
        Object secondTeam = executeCommandInShell(command);
        assertThat(secondTeam, is("Team with name '" + teamName + "' already exists"));
    }

    @Test
    public void add_team_to_tournament_command_works() {
        final String tournamentName = "Test Tournament";
        String createTournamentCommand = String.format("create-tournament '%s'", tournamentName);
        executeCommandInShell(createTournamentCommand);
        final String teamName = "Team Java";
        Object addTeam = executeCommandInShell("add-team-to-tournament '" + teamName + "' '" + tournamentName + "'");
        assertThat(addTeam, is("Team with name '" + teamName +"' added to tournament '" + tournamentName + "'"));
    }

    @Test
    public void add_team_to_tournament_if_tournament_does_not_exist_displays_error() {
        final String tournamentName = "Tournament";
        final String teamName = "Team Java";
        Object addTeam = executeCommandInShell("add-team-to-tournament '" + teamName + "' '" + tournamentName + "'");
        assertThat(addTeam, is("Tournament with name '" + tournamentName + "' does not exist"));
    }

    @Test
    public void add_team_to_tournament_twice_displays_error() {
        final String tournamentName = "Test Tournament";
        String createTournamentCommand = String.format("create-tournament '%s'", tournamentName);
        executeCommandInShell(createTournamentCommand);
        final String teamName = "Team Java";
        String addTeamCommand = String.format("add-team-to-tournament '%s' '%s'", teamName, tournamentName);        
        executeCommandInShell(addTeamCommand);
        Object addTeamAgain = executeCommandInShell(addTeamCommand);
        assertThat(addTeamAgain, is("Team with name '" + teamName +"' is already in tournament '" + tournamentName + "'"));
    }

    @Test
    public void create_person_command_works() {
        final String personName = "Jane Doe";     
        final String command = String.format("create-person '%s'", personName);   
        Object createPerson = executeCommandInShell(command);
        assertThat(createPerson, is("Person with name '" + personName + "' created"));
    }

    @Test
    public void create_person_already_added_displays_error() {
        final String personName = "Jane Doe";
        final String command = String.format("create-person '%s'", personName);
        Object firstPerson = executeCommandInShell(command);
        assertThat(firstPerson, is("Person with name '" + personName + "' created"));
        Object secondPerson = executeCommandInShell(command);
        assertThat(secondPerson, is("Person with name '" + personName + "' already exists"));
    }

    @Test
    public void add_coach_to_team_command_works() {
        final String coachName = "Coach Smith";
        final String teamName = "Test Team";
        final String command = String.format("add-coach-to-team '%s' '%s'", coachName, teamName);
        Object addCoach = executeCommandInShell(command);
        assertThat(addCoach, is("Person with name '" + coachName + "' added as a coach to team '" + teamName + "'"));
    }

    @Test
    public void add_coach_twice_displays_error() {
        final String coachName = "Coach Smith";
        final String teamName = "Test Team";
        final String command = String.format("add-coach-to-team '%s' '%s'", coachName, teamName);
        executeCommandInShell(command);
        Object addCoachAgain = executeCommandInShell(command);
        assertThat(addCoachAgain, is("Person with name '" + coachName + "' is already a coach on team '" + teamName + "'"));
    }

    @Test
    public void add_player_to_team_command_works() {
        final String playerName = "Jane Doe";
        final String teamName = "Test Team";
        final int playerNumber = 10;
        final String command = String.format("add-player-to-team '%s' '%s' '%d'", playerName, teamName, playerNumber);        
        Object addPlayer = executeCommandInShell(command);
        assertThat(addPlayer, is("Person with name '" + playerName + "' added as a player to team '" + teamName + "' with number '" + playerNumber + "'"));
    }

    @Test
    public void add_player_twice_displays_error() {
        final String playerName = "Jane Doe";
        final String teamName = "Test Team";
        final int playerNumber = 10;
        final String command = String.format("add-player-to-team '%s' '%s' '%d'", playerName, teamName, playerNumber);        
        executeCommandInShell(command);
        Object addPlayer = executeCommandInShell(command);
        assertThat(addPlayer, is("Person with name '" + playerName + "' is already a player on team '" + teamName + "'"));
    }

    @Test
    public void add_player_with_same_number_displays_error() {
        final String player1Name = "Player One";
        final String player2Name = "Player Two";
        final String teamName = "Test Team";
        final int playerNumber = 10;
        executeCommandInShell(String.format("add-player-to-team '%s' '%s' '%d'", player1Name, teamName, playerNumber));
        String command = String.format("add-player-to-team '%s' '%s' '%d'", player2Name, teamName, playerNumber);
        Object addPlayer = executeCommandInShell(command);
        assertThat(addPlayer, is("Player with number '" + playerNumber + "' is already on team '" + teamName + "'"));
    }

    @Test
    public void add_game_to_tournament_command_works() {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";        
        executeCommandInShell(String.format("create-tournament '%s'", tournamentName));
        String command = String.format("add-game-to-tournament '%s' '%s' '%s'", team1Name, team2Name, tournamentName);
        Object addGame = executeCommandInShell(command);
        assertThat(addGame, is("Game between teams '" + team1Name + "' and '" + team2Name + "' added to tournament '" + tournamentName + "'"));
    }

    @Test
    public void add_game_to_tournament_that_does_not_exist_displays_error() {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        String command = String.format("add-game-to-tournament '%s' '%s' '%s'", team1Name, team2Name, tournamentName);
        Object addGame = executeCommandInShell(command);
        assertThat(addGame, is("Tournament with name '" + tournamentName + "' does not exist"));
    }

    @Test
    public void add_game_twice_to_tournament_displays_error() {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        executeCommandInShell(String.format("create-tournament %s", tournamentName));
        String command = String.format("add-game-to-tournament '%s' '%s' '%s'", team1Name, team2Name, tournamentName);
        executeCommandInShell(command);
        Object addGameAgain = executeCommandInShell(command);
        assertThat(addGameAgain, is("Game between teams '" + team1Name + "' and '" + team2Name + "' already exists in tournament '" + tournamentName + "'"));
    }

    @Test
    public void add_game_where_team_plays_itself_displays_error() {
        final String tournamentName = "Test Tournament";
        final String teamName = "One and only Team";
        executeCommandInShell(String.format("create-tournament '%s'", tournamentName));
        String command = String.format("add-game-to-tournament '%s' '%s' '%s'", teamName, teamName, tournamentName);
        Object addInvalidGame = executeCommandInShell(command);
        assertThat(addInvalidGame, is("The team '" + teamName + "' can not play itself ('" + teamName + "') in tournament '" + tournamentName + "'"));
    }

    @Test
    public void score_command_works() {
        final String tournamentName = "Test Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final int team1Points = 1;
        final int team2Points = 2;
        executeCommandInShell(String.format("create-tournament '%s'", tournamentName));
        executeCommandInShell(String.format("add-game-to-tournament '%s' '%s' '%s'", team1Name, team2Name, tournamentName));
        String command = String.format("score-game-in-tournament '%s' %d '%s' %d '%s'", team1Name, team1Points, team2Name, team2Points, tournamentName);
        Object scoreGame = executeCommandInShell(command);
        assertThat(scoreGame, is("Score for game in tournament '" + tournamentName + "' set: team '" + team1Name + "' scored " + team1Points + " point(s) and team '" + team2Name + "' scored " + team2Points + " point(s)"));     
    }

    @Test
    public void score_game_in_tournament_that_does_not_exist_displays_error() {
        final String tournamentName = "Test Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final int team1Points = 1;
        final int team2Points = 2;
        executeCommandInShell(String.format("add-game-to-tournament '%s' '%s' '%s'", team1Name, team2Name, tournamentName));
        String command = String.format("score-game-in-tournament '%s' %d '%s' %d '%s'", team1Name, team1Points, team2Name, team2Points, tournamentName);
        Object scoreGame = executeCommandInShell(command);
        assertThat(scoreGame, is("Tournament with name '" + tournamentName + "' does not exist"));
    }

    @Test
    public void score_game_when_team_does_not_exist_displays_error() {
        final String tournamentName = "Test Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team that is not in game";
        final int team1Points = 1;
        final int team2Points = 2;
        executeCommandInShell(String.format("create-tournament '%s'", tournamentName));
        executeCommandInShell(String.format("add-game-to-tournament '%s' '%s' '%s'", team1Name, team2Name, tournamentName));
        String command = String.format("score-game-in-tournament '%s' %d '%s' %d '%s'", team1Name, team1Points, team3Name, team2Points, tournamentName);
        Object scoreGame = executeCommandInShell(command);
        assertThat(scoreGame, is("Score for game in tournament '" + tournamentName + "' NOT set because either team '" + team1Name + "' or team '" + team3Name + "' does not exist"));
    }

    @Test
    public void score_game_when_game_does_not_exist_displays_error() {
        final String tournamentName = "Test Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final int team1Points = 1;
        final int team2Points = 2;
        executeCommandInShell(String.format("create-tournament '%s'", tournamentName));     
        executeCommandInShell(String.format("add-team-to-tournament '%s' '%s'", team1Name, tournamentName));
        executeCommandInShell(String.format("add-team-to-tournament '%s' '%s'", team2Name, tournamentName));

        String command = String.format("score-game-in-tournament '%s' %d '%s' %d '%s'", team1Name, team1Points, team2Name, team2Points, tournamentName);
        Object scoreGame = executeCommandInShell(command);
        assertThat(scoreGame, is("Score for game between team '" + team1Name + "' and team '" + team2Name + "' NOT set because that game does not exist in tournament '" + tournamentName + "'"));     
    }

    @Test
    public void invalid_score_displays_error() {
        final String tournamentName = "Test Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final int team1Points = 1;
        final int team2Points = -2;
        executeCommandInShell(String.format("create-tournament '%s'", tournamentName));
        executeCommandInShell(String.format("add-game-to-tournament '%s' '%s' '%s'", team1Name, team2Name, tournamentName));
        String command = String.format("score-game-in-tournament '%s' %d '%s' %d '%s'", team1Name, team1Points, team2Name, team2Points, tournamentName);
        Object scoreGame = executeCommandInShell(command);
        assertThat(scoreGame, is("Score for game in tournament '" + tournamentName + "' NOT set because the score is invalid: team '" + team1Name + "' scored " + team1Points + " point(s) and team '" + team2Name + "' scored " + team2Points + " point(s)"));
    }

    @Test
    public void report_game_results_works() {
        final String tournamentName = "Test Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final int team1Points = 1;
        final int team2Points = 2;
        executeCommandInShell(String.format("create-tournament '%s'", tournamentName));
        executeCommandInShell(String.format("add-game-to-tournament '%s' '%s' '%s'", team1Name, team2Name, tournamentName));
        executeCommandInShell(String.format("score-game-in-tournament '%s' %d '%s' %d '%s'", team1Name, team1Points, team2Name, team2Points, tournamentName));        

        String command = String.format("report-tournament-results '%s'", tournamentName);
        Object report = executeCommandInShell(command);
        assertThat((String)report, is(not(emptyString())));
    }

    private Object executeCommandInShell(String commandString) {
        try(FileInputProvider inputProvider = new FileInputProvider(new StringReader(commandString), parser)) {
            Input input = inputProvider.readInput();
            Object command = shell.evaluate(input);
            resultHandler.handleResult(command);
            return command;
        } catch(IOException ioe) {
            return null;
        }
    }
}
