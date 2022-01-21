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
    public void add_tournament_command_works() {
        final String tournamentName = "Tournament";
        Object createTournament = executeCommandInShell("create-tournament " + tournamentName);
		assertThat(createTournament, is("Tournament with name '" + tournamentName + "' created"));
    }

    @Test
    public void add_tournament_with_spaces_and_quotes_works() {
        final String tournamentName = "Test Tournament with \"spaces\"";
        final String stringCommand = "create-tournament '" + tournamentName + "'";
        Object createTournament = executeCommandInShell(stringCommand);        
        assertThat(createTournament, is("Tournament with name '" + tournamentName + "' created"));
    }

    @Test
    public void add_tournament_already_added_displays_error() {
        final String tournamentName = "SameName";
        Object firstTournament = executeCommandInShell("create-tournament " + tournamentName);
        assertThat(firstTournament, is("Tournament with name '" + tournamentName + "' created"));
        Object secondTournament = executeCommandInShell("create-tournament " + tournamentName);
        assertThat(secondTournament, is("Tournament with name '" + tournamentName + "' already exists"));
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
