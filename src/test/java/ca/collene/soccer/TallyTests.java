package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;

import ca.collene.soccer.models.Tally;
import ca.collene.soccer.models.Tally.TallyType;

@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
public class TallyTests {
    @Test
    public void tally_type_list_constructor_works() {
        final String teamName = "Team";
        final List<TallyType> tallyTypes = Arrays.asList(TallyType.LOSS, 
                                                    TallyType.TIE,
                                                    TallyType.WIN,
                                                    TallyType.WIN,
                                                    TallyType.LOSS,
                                                    TallyType.WIN
                                    );
        Tally tally = new Tally(teamName, tallyTypes);
        assertThat(tally.getWins(), is(equalTo(3L)));
        assertThat(tally.getLosses(), is(equalTo(2L)));
        assertThat(tally.getTies(), is(equalTo(1L)));
        assertThat(tally.getUnscored(), is(equalTo(0L)));
    }

    @Test
    public void tally_total_works() {
        final String teamName = "Team";
        final List<TallyType> tallyTypes = Arrays.asList(TallyType.LOSS, 
                                                    TallyType.TIE,
                                                    TallyType.WIN,
                                                    TallyType.WIN,
                                                    TallyType.LOSS,
                                                    TallyType.WIN
                                    );
        Tally tally = new Tally(teamName, tallyTypes);
        assertThat(tally.getTotal(), is(equalTo(13L)));
    }
}
