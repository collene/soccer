package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;

import ca.collene.soccer.entities.Game;
import ca.collene.soccer.entities.Team;
import ca.collene.soccer.models.Tally.TallyType;

@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
public class GameTests {
    // has team works
    @Test
    public void has_team_works() {
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team Three";

        Team team1 = new Team.With().name(team1Name).build();
        Team team2 = new Team.With().name(team2Name).build();
        Team team3 = new Team.With().name(team3Name).build();
        Game game = new Game.With().teams(team1, team2).build();
        assertTrue(game.hasTeam(team1));
        assertTrue(game.hasTeam(team2));
        assertFalse(game.hasTeam(team3));
    }

    // has score works
    @Test
    public void has_score_works() throws Exception {
        final String team1Name = "Team One";
        final String team2Name = "Team Two";    
        final int team1Points = 1;
        final int team2Points = 2;    

        Team team1 = new Team.With().name(team1Name).build();
        Team team2 = new Team.With().name(team2Name).build();

        Game game = new Game.With().teams(team1, team2).build();
        assertFalse(game.hasScore());
        game.setScore(team1, team1Points, team2, team2Points);
        assertTrue(game.hasScore());
    }

    // get points for team works
    @Test
    public void get_points_for_team_works() throws Exception {
        final String team1Name = "Team One";
        final String team2Name = "Team Two";    
        final int team1Points = 1;
        final int team2Points = 2;    

        Team team1 = new Team.With().name(team1Name).build();
        Team team2 = new Team.With().name(team2Name).build();

        Game game = new Game.With().teams(team1, team2).build();        
        game.setScore(team1, team1Points, team2, team2Points);
        assertThat(game.getPointsForTeam(team1), is(equalTo(team1Points)));
        assertThat(game.getPointsForTeam(team2), is(equalTo(team2Points)));
    }

    // get points for other team works
    @Test
    public void get_points_for_other_team_works() throws Exception {
        final String team1Name = "Team One";
        final String team2Name = "Team Two";    
        final int team1Points = 1;
        final int team2Points = 2;    

        Team team1 = new Team.With().name(team1Name).build();
        Team team2 = new Team.With().name(team2Name).build();

        Game game = new Game.With().teams(team1, team2).build();        
        game.setScore(team1, team1Points, team2, team2Points);
        assertThat(game.getPointsForOtherTeam(team1), is(equalTo(team2Points)));
        assertThat(game.getPointsForOtherTeam(team2), is(equalTo(team1Points)));
    }

    // get tally type works
    @Test
    public void get_tally_type_works() throws Exception {
        final String team1Name = "Team One";
        final String team2Name = "Team Two";    
        final String team3Name = "Team Three";
        final int team1Points = 1;
        final int team2Points = 2;    
        final int team3Points = 1;

        Team team1 = new Team.With().name(team1Name).build();
        Team team2 = new Team.With().name(team2Name).build();
        Team team3 = new Team.With().name(team3Name).build();

        Game game1 = new Game.With().teams(team1, team2).build();     
        game1.setScore(team1, team1Points, team2, team2Points);
        assertThat(game1.getTallyTypeForTeam(team1), is(TallyType.LOSS));
        assertThat(game1.getTallyTypeForTeam(team2), is(TallyType.WIN));

        Game game2 = new Game.With().teams(team1, team3).build();
        game2.setScore(team1, team1Points, team3, team3Points);
        assertThat(game2.getTallyTypeForTeam(team1), is(TallyType.TIE));
        assertThat(game2.getTallyTypeForTeam(team3), is(TallyType.TIE));
    }
}
