package ca.collene.soccer.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import ca.collene.soccer.services.TournamentNameAlreadyExistsException;
import ca.collene.soccer.services.TournamentService;

@ShellComponent
public class Commands {
    @Autowired
    private TournamentService tournamentService;

    @ShellMethod(value = "Create new tournament with specified name.", group = "Tournament Commands")
    public String createTournament(@ShellOption({"-N, '--name"}) String name) {
        try {
            tournamentService.createTournament(name);
            return String.format("Tournament with name '%s' created", name);
        } catch(TournamentNameAlreadyExistsException e) {
            return String.format("Tournament with name '%s' already exists", name);
        }        
    }

    @ShellMethod(value = "Echo Message")
	public void echo(String message) {
		System.out.println(message);
	}
}
