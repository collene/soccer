package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import ca.collene.soccer.entities.Team;
import ca.collene.soccer.repositories.TeamRepository;
import ca.collene.soccer.services.NameAlreadyExistsException;
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
    public void get_team_by_name_that_does_not_exist_throws_exception() {
        final String testTeamName = "Team that does not exist";
        assertThrows(TeamDoesNotExistException.class, () -> {
            teamService.getTeam(testTeamName);
        });
    }
}
