package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.repositories.TournamentRepository;
import ca.collene.soccer.services.TournamentNameAlreadyExistsException;
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
    private TournamentRepository tournamentRepository;
    
    @Test
    public void create_tournament_works() {
        final String testTournamentName = "Test tournament";
        assertThat(tournamentService.getTournament(testTournamentName), is(nullValue()));

        assertDoesNotThrow(() -> {
            Tournament newTournament = tournamentService.createTournament(testTournamentName);
            assertThat(tournamentService.getTournament(testTournamentName), is(equalTo(newTournament)));
            assertThat(tournamentRepository.count(), is(equalTo(1l)));
        });        
    }

    @Test
    public void create_tournament_with_duplicate_name_fails() {
        final String sameTournamentName = "Test tournament";
        assertDoesNotThrow(() -> {
            tournamentService.createTournament(sameTournamentName);
        });
        
        assertThat(tournamentRepository.count(), is(equalTo(1l)));

        // make sure the exception is thrown
        assertThrows(TournamentNameAlreadyExistsException.class, () -> {
            tournamentService.createTournament(sameTournamentName);
        });

        // make sure that a second tournament hasn't been added after the exception was thrown
        assertThat(tournamentRepository.count(), is(equalTo(1l)));
    }
}
