package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.shell.result.DefaultResultHandler;

import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.repositories.TournamentRepository;

@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
class SoccerApplicationTests {

	@Autowired
	private Shell shell;

	@Autowired
    private DefaultResultHandler resultHandler;

	@Autowired
	private TournamentRepository tournamentRepository;

	@Test
	void context_loads() {
		
	}

	@Test
	void shell_starts_and_is_interactive() {
		Object help = shell.evaluate(() -> "help");		
		resultHandler.handleResult(help);
        assertThat(help, is(notNullValue()));
	}

	@Test
	void repositories_created_and_save() {
		assertThat(tournamentRepository.count(), is(0l));

		final String testTournamentName = "Test tournament";		
		Tournament newTournament = tournamentRepository.save(new Tournament(testTournamentName));
		assertThat(newTournament.getId(), is(notNullValue()));

		assertThat(tournamentRepository.count(), is(1l));
		Tournament tournament = tournamentRepository.findByName(testTournamentName);
		assertThat(tournament, is(equalTo(newTournament)));
	}
}
