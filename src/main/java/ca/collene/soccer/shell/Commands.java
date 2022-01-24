package ca.collene.soccer.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.services.CoachAlreadyOnTeamException;
import ca.collene.soccer.services.GameAlreadyInTournamentException;
import ca.collene.soccer.services.NameAlreadyExistsException;
import ca.collene.soccer.services.NumberAlreadyInUseException;
import ca.collene.soccer.services.PersonService;
import ca.collene.soccer.services.PlayerAlreadyOnTeamException;
import ca.collene.soccer.services.TeamAlreadyInTournamentException;
import ca.collene.soccer.services.TeamService;
import ca.collene.soccer.services.TournamentDoesNotExistException;
import ca.collene.soccer.services.TournamentService;

@ShellComponent
public class Commands {
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonService personService;

    @ShellMethod(value = "Create new tournament with specified name.", group = "Tournament Commands")
    public String createTournament(@ShellOption({"-N", "--name"}) String name) {
        try {
            tournamentService.createTournament(name);
            return String.format("Tournament with name '%s' created", name);
        } catch(NameAlreadyExistsException e) {
            return String.format("Tournament with name '%s' already exists", name);
        }        
    }

    @ShellMethod(value = "Add team to tournament.", group = "Tournament Commands")
    public String addTeamToTournament(@ShellOption({"--team"}) String teamName, @ShellOption({"--tournament"}) String tournamentName) {
        try {
            Tournament tournament = tournamentService.getTournament(tournamentName);
            tournamentService.addTeamToTournament(teamName, tournament);
            return String.format("Team with name '%s' added to tournament '%s'", teamName, tournamentName);
        } catch (TournamentDoesNotExistException e) {            
            return String.format("Tournament with name '%s' does not exist", tournamentName);
        } catch (TeamAlreadyInTournamentException e) {
            return String.format("Team with name '%s' is already in tournament '%s'", teamName, tournamentName);
        }                    
    }    

    @ShellMethod(value = "Create new team with specified name.", group = "Team Commands")
    public String createTeam(@ShellOption({"-N", "--name"}) String name) {
        try {
            teamService.createTeam(name);
            return String.format("Team with name '%s' created", name);
        } catch(NameAlreadyExistsException e) {
            return String.format("Team with name '%s' already exists", name);
        }
    }

    @ShellMethod(value = "Create new person with specified name.", group = "Person Commands")
    public String createPerson(@ShellOption({"-N", "--name"}) String name) {
        try {
            personService.createPerson(name);
            return String.format("Person with name '%s' created", name);
        } catch(NameAlreadyExistsException e) {
            return String.format("Person with name '%s' already exists", name);
        }
    }

    @ShellMethod(value = "Add coach to team.", group = "Team Commands")
    public String addCoachToTeam(@ShellOption({"--coach"})String personName, @ShellOption({"--team"})String teamName) {
        try {
            teamService.addCoachToTeam(personName, teamName);
            return String.format("Person with name '%s' added as a coach to team '%s'", personName, teamName);
        } catch(CoachAlreadyOnTeamException e) {
            return String.format("Person with name '%s' is already a coach on team '%s'", personName, teamName);
        }
    }

    @ShellMethod(value = "Add player to team.", group = "Team Commands")
    public String addPlayerToTeam(@ShellOption({"--player"}) String personName, 
                                    @ShellOption({"--team"}) String teamName, 
                                    @ShellOption({"--number"}) int playerNumber) {
        try {
            teamService.addPlayerToTeam(personName, teamName, playerNumber);
            return String.format("Person with name '%s' added as a player to team '%s' with number '%d'", personName, teamName, playerNumber);
        } catch(PlayerAlreadyOnTeamException e) {
            return String.format("Person with name '%s' is already a player on team '%s'", personName, teamName);
        } catch(NumberAlreadyInUseException e) {
            return String.format("Player with number '%d' is already on team '%s'", playerNumber, teamName);
        }
    }

    @ShellMethod(value = "Add game between two teams to tournament.", group = "Tournament Commands")
    public String addGameToTournament(@ShellOption({"--team1"}) String team1Name, 
                                        @ShellOption({"--team2"}) String team2Name, 
                                        @ShellOption({"--tournament"}) String tournamentName) {
        try {
            Tournament tournament = tournamentService.getTournament(tournamentName);
            tournamentService.addGameToTournament(team1Name, team2Name, tournament);
            return String.format("Game between teams '%s' and '%s' added to tournament '%s'", team1Name, team2Name, tournamentName);
        } catch (TournamentDoesNotExistException e) {
            return String.format("Tournament with name '%s' does not exist", tournamentName);
        } catch (GameAlreadyInTournamentException e) {
            return String.format("Game between teams '%s' and '%s' already exists in tournament '%s'", team1Name, team2Name, tournamentName);
        }        
    }
}
