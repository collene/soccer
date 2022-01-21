package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
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
import ca.collene.soccer.services.TournamentService;

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

	@Autowired
	private TournamentService tournamentService;

	@Test
	void contextLoads() {
		
	}

	@Test
	void helpDisplays() {
		Object help = shell.evaluate(() -> "help");		
		resultHandler.handleResult(help);
        assertThat(help, is(notNullValue()));
	}

	@Test
	void repositoriesCreatedAndSave() {
		assertThat(tournamentRepository.count(), is(0l));

		Tournament newTournament = tournamentService.createTournament("Testing tournament");
		assertThat(newTournament.getId(), is(notNullValue()));

		assertThat(tournamentRepository.count(), is(1l));
		Tournament tournament = tournamentService.getTournament("Testing tournament");
		assertThat(tournament, is(equalTo(newTournament)));
	}

	@Test
	void addWorks() {
		Object add = shell.evaluate(() -> "add 1 2");
		resultHandler.handleResult(add);
		assertThat(add, is(3));
	}
}
