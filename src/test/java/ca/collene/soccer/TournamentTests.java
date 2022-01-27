package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;

import ca.collene.soccer.entities.Team;
import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.models.Tally;
import ca.collene.soccer.models.Tally.TallyType;

@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
public class TournamentTests {
    private Logger logger = LoggerFactory.getLogger(Tournament.class);

    // has team works
    @Test
    public void has_team_works() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team Three";
        Tournament tournament = Tournament.builder().name(tournamentName).build();
        Team team1 = Team.builder().name(team1Name).build();
        Team team2 = Team.builder().name(team2Name).build();
        Team team3 = Team.builder().name(team3Name).build();
        tournament.addTeam(team1);
        tournament.addTeam(team2);

        assertTrue(tournament.hasTeam(team1));
        assertTrue(tournament.hasTeam(team2));
        assertFalse(tournament.hasTeam(team3));
    }

    // has game with teams works
    @Test
    public void has_game_with_teams_works() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team Three";
        Tournament tournament = Tournament.builder().name(tournamentName).build();
        Team team1 = Team.builder().name(team1Name).build();
        Team team2 = Team.builder().name(team2Name).build();
        Team team3 = Team.builder().name(team3Name).build();
        tournament.addGame(team1, team2);
        tournament.addGame(team1, team3);        

        assertTrue(tournament.hasGameWithTeams(team1, team2));
        assertTrue(tournament.hasGameWithTeams(team2, team1));
        assertTrue(tournament.hasGameWithTeams(team1, team3));
        assertFalse(tournament.hasGameWithTeams(team2, team3));
        assertFalse(tournament.hasGameWithTeams(team3, team2));
    }

    // get team games works
    @Test
    public void get_games_for_team_works() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team Three";
        final String team4Name = "Team Four";
        final String team5Name = "Team Five";
        Tournament tournament = Tournament.builder().name(tournamentName).build();
        Team team1 = Team.builder().name(team1Name).build();
        Team team2 = Team.builder().name(team2Name).build();
        Team team3 = Team.builder().name(team3Name).build();
        Team team4 = Team.builder().name(team4Name).build();
        Team team5 = Team.builder().name(team5Name).build();
        tournament.addGame(team1, team2);
        tournament.addGame(team1, team3);      
        tournament.addGame(team1, team4);
        tournament.addGame(team2, team3);
        tournament.addGame(team2, team4);
        
        assertThat(tournament.getGamesForTeam(team1), hasSize(3));
        assertThat(tournament.getGamesForTeam(team2), hasSize(3));
        assertThat(tournament.getGamesForTeam(team3), hasSize(2));
        assertThat(tournament.getGamesForTeam(team4), hasSize(2));
        assertThat(tournament.getGamesForTeam(team5), is(empty()));
    }

    @Test
    public void get_tally_types_for_team_works() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team Three";
        final String team4Name = "Team Four";
        final String team5Name = "Team Five";
        final int team1Points = 1;
        final int team2Points = 2;
        final int team3Points = 3;
        final int team4Points = 4;
        final int team5Points = 5;
        Tournament tournament = Tournament.builder().name(tournamentName).build();
        Team team1 = Team.builder().name(team1Name).build();
        Team team2 = Team.builder().name(team2Name).build();
        Team team3 = Team.builder().name(team3Name).build();
        Team team4 = Team.builder().name(team4Name).build();
        Team team5 = Team.builder().name(team5Name).build();
        tournament.addGame(team1, team2);
        tournament.addGame(team1, team3);      
        tournament.addGame(team1, team4);
        tournament.addGame(team2, team3);
        tournament.addGame(team2, team4);
        tournament.addGame(team3, team4);
        tournament.addGame(team3, team5);
        tournament.addGame(team4, team5);
        tournament.scoreGame(team1, team1Points, team2, team2Points);   // T1: L, T2: W
        tournament.scoreGame(team1, team1Points, team3, team3Points);   // T1: L, T3: W
        tournament.scoreGame(team1, team1Points, team4, team4Points);   // T1: L, T4: W
        tournament.scoreGame(team2, team2Points, team3, team3Points);   // T2: L, T3: W
        tournament.scoreGame(team2, team2Points, team4, team4Points);   // T2: L, T4: W
        tournament.scoreGame(team3, team3Points, team4, team4Points);   // T3: L, T4: W
        tournament.scoreGame(team3, team3Points, team5, team5Points);   // T3: L, T5: W
        // last game is unscored
        assertThat(tournament.getTallyTypesForTeam(team1), containsInAnyOrder(TallyType.LOSS, TallyType.LOSS, TallyType.LOSS));
        assertThat(tournament.getTallyTypesForTeam(team2), containsInAnyOrder(TallyType.WIN, TallyType.LOSS, TallyType.LOSS));
        assertThat(tournament.getTallyTypesForTeam(team3), containsInAnyOrder(TallyType.WIN, TallyType.WIN, TallyType.LOSS, TallyType.LOSS));
        assertThat(tournament.getTallyTypesForTeam(team4), containsInAnyOrder(TallyType.WIN, TallyType.WIN, TallyType.WIN, TallyType.UNSCORED));
        assertThat(tournament.getTallyTypesForTeam(team5), containsInAnyOrder(TallyType.WIN, TallyType.UNSCORED));
    }

    @Test
    public void get_tally_works() throws Exception {
        final String tournamentName = "Tournament";
        final String team1Name = "Team One";
        final String team2Name = "Team Two";
        final String team3Name = "Team Three";
        final String team4Name = "Team Four";
        final String team5Name = "Team Five";
        final int team1Points = 1;
        final int team2Points = 2;
        final int team3Points = 3;
        final int team4Points = 4;
        final int team5Points = 5;
        Tournament tournament = Tournament.builder().name(tournamentName).build();        
        Team team1 = Team.builder().name(team1Name).build();
        Team team2 = Team.builder().name(team2Name).build();
        Team team3 = Team.builder().name(team3Name).build();
        Team team4 = Team.builder().name(team4Name).build();
        Team team5 = Team.builder().name(team5Name).build();
        tournament.addTeam(team1);
        tournament.addTeam(team2);
        tournament.addTeam(team3);
        tournament.addTeam(team4);
        tournament.addTeam(team5);
        tournament.addGame(team1, team2);
        tournament.addGame(team1, team3);      
        tournament.addGame(team1, team4);
        tournament.addGame(team2, team3);
        tournament.addGame(team2, team4);
        tournament.addGame(team3, team4);
        tournament.addGame(team3, team5);
        tournament.addGame(team4, team5);
        tournament.scoreGame(team1, team1Points, team2, team2Points);   // T1: L, T2: W
        tournament.scoreGame(team1, team1Points, team3, team3Points);   // T1: L, T3: W
        tournament.scoreGame(team1, team1Points, team4, team4Points);   // T1: L, T4: W
        tournament.scoreGame(team2, team2Points, team3, team3Points);   // T2: L, T3: W
        tournament.scoreGame(team2, team2Points, team4, team4Points);   // T2: L, T4: W
        tournament.scoreGame(team3, team3Points, team4, team4Points);   // T3: L, T4: W
        tournament.scoreGame(team3, team3Points, team5, team5Points);   // T3: L, T5: W
        // last game is unscored

        List<Tally> tallies = tournament.getTally();
        assertThat(tallies, hasSize(5));

        // in this case, our tally sort order matters
        Tally team1Tally = tallies.get(3);
        Tally team2Tally = tallies.get(2);
        Tally team3Tally = tallies.get(1);
        Tally team4Tally = tallies.get(0);
        Tally team5Tally = tallies.get(4);

        for(Tally tally: tallies) {
            logger.debug(tally.toString());
        }

        assertThat(team1Tally, is(equalTo(Tally.builder()
                                        .teamName(team1Name)
                                        .losses(3l)                                        
                                    .build())));
        assertThat(team1Tally.getTotal(), is(equalTo(3l)));

        assertThat(team2Tally, is(equalTo(Tally.builder()
                                        .teamName(team2Name)
                                        .wins(1l)
                                        .losses(2l)
                                    .build())));
        assertThat(team2Tally.getTotal(), is(equalTo(5l)));

        assertThat(team3Tally, is(equalTo(Tally.builder()
                                        .teamName(team3Name)
                                        .wins(2l)
                                        .losses(2l)
                                    .build())));
        assertThat(team3Tally.getTotal(), is(equalTo(8l)));

        assertThat(team4Tally, is(equalTo(Tally.builder()
                                        .teamName(team4Name)
                                        .wins(3l)
                                        .unscored(1l)                                        
                                    .build())));
        assertThat(team4Tally.getTotal(), is(equalTo(9l)));

        assertThat(team5Tally, is(equalTo(Tally.builder()
                                        .teamName(team5Name)
                                        .wins(1l)
                                        .unscored(1l)
                                    .build())));
        assertThat(team5Tally.getTotal(), is(equalTo(3l)));
    }
}
