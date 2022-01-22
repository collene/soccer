package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
