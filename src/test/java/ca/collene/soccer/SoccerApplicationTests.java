package ca.collene.soccer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.shell.result.DefaultResultHandler;

@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
class SoccerApplicationTests {

	@Autowired
	private Shell shell;

	@Autowired
    private DefaultResultHandler resultHandler;

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
	void addWorks() {
		Object add = shell.evaluate(() -> "add 1 2");
		resultHandler.handleResult(add);
		assertThat(add, is(3));
	}
}
