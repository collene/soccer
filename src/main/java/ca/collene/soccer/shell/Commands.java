package ca.collene.soccer.shell;

import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.models.Tally;
import ca.collene.soccer.services.CoachAlreadyOnTeamException;
import ca.collene.soccer.services.GameAlreadyInTournamentException;
import ca.collene.soccer.services.GameDoesNotExistException;
import ca.collene.soccer.services.InvalidGameException;
import ca.collene.soccer.services.InvalidScoreException;
import ca.collene.soccer.services.NameAlreadyExistsException;
import ca.collene.soccer.services.NumberAlreadyInUseException;
import ca.collene.soccer.services.PersonService;
import ca.collene.soccer.services.PlayerAlreadyOnTeamException;
import ca.collene.soccer.services.TeamAlreadyInTournamentException;
import ca.collene.soccer.services.TeamDoesNotExistException;
import ca.collene.soccer.services.TeamService;
import ca.collene.soccer.services.TournamentDoesNotExistException;
import ca.collene.soccer.services.TournamentService;

@ShellComponent
public class Commands {
    private Logger logger = LoggerFactory.getLogger(Commands.class);

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonService personService;

    @ShellMethod(value = "Create new tournament with specified name.", group = "Tournament Commands")
    public String createTournament(@ShellOption(value = {"-N", "--name"}, help ="Name of the tournament.") String name) {
        try {
            tournamentService.createTournament(name);
            return String.format("Tournament with name '%s' created", name);
        } catch(NameAlreadyExistsException e) {
            return String.format("Tournament with name '%s' already exists", name);
        }        
    }

    @ShellMethod(value = "Add team to tournament.", group = "Tournament Commands")
    public String addTeamToTournament(@ShellOption(value = {"--team"}, help = "Name of the team.") String teamName, 
                                        @ShellOption(value = {"--tournament"}, help = "Name of the tournament.") String tournamentName) {
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
    public String createTeam(@ShellOption(value = {"-N", "--name"}, help = "Name of the team.") String name) {
        try {
            teamService.createTeam(name);
            return String.format("Team with name '%s' created", name);
        } catch(NameAlreadyExistsException e) {
            return String.format("Team with name '%s' already exists", name);
        }
    }

    @ShellMethod(value = "Create new person with specified name.", group = "Person Commands")
    public String createPerson(@ShellOption(value = {"-N", "--name"}, help = "Name of the person.") String name) {
        try {
            personService.createPerson(name);
            return String.format("Person with name '%s' created", name);
        } catch(NameAlreadyExistsException e) {
            return String.format("Person with name '%s' already exists", name);
        }
    }

    @ShellMethod(value = "Add coach to team.", group = "Team Commands")
    public String addCoachToTeam(@ShellOption(value = {"--coach"}, help = "Name of the coach.")String personName, 
                                    @ShellOption(value = {"--team"}, help = "Name of the team.")String teamName) {
        try {
            teamService.addCoachToTeam(personName, teamName);
            return String.format("Person with name '%s' added as a coach to team '%s'", personName, teamName);
        } catch(CoachAlreadyOnTeamException e) {
            return String.format("Person with name '%s' is already a coach on team '%s'", personName, teamName);
        }
    }

    @ShellMethod(value = "Add player to team.", group = "Team Commands")
    public String addPlayerToTeam(@ShellOption(value = {"--player"}, help = "Name of the player.") String personName, 
                                    @ShellOption(value = {"--team"}, help = "Name of the team.") String teamName, 
                                    @ShellOption(value = {"--number"}, help = "Number of the player on this team.") int playerNumber) {
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
    public String addGameToTournament(@ShellOption(value = {"--team1"}, help = "Name of the first team.") String team1Name, 
                                        @ShellOption(value = {"--team2"}, help = "Name of the second team.") String team2Name, 
                                        @ShellOption(value = {"--tournament"}, help = "Name of the tournament.") String tournamentName) {
        try {
            Tournament tournament = tournamentService.getTournament(tournamentName);
            tournamentService.addGameToTournament(team1Name, team2Name, tournament);
            return String.format("Game between teams '%s' and '%s' added to tournament '%s'", team1Name, team2Name, tournamentName);
        } catch (TournamentDoesNotExistException e) {
            return String.format("Tournament with name '%s' does not exist", tournamentName);
        } catch (GameAlreadyInTournamentException e) {
            return String.format("Game between teams '%s' and '%s' already exists in tournament '%s'", team1Name, team2Name, tournamentName);
        } catch (InvalidGameException e) {
            return String.format("The team '%s' can not play itself ('%s') in tournament '%s'", team1Name, team2Name, tournamentName);
        }     
    }

    @ShellMethod(value = "Score game between two teams in tournament.", group = "Tournament Commands")
    public String scoreGameInTournament(@ShellOption(value = {"--team1"}, help = "Name of the first team.") String team1Name,
                                        @ShellOption(value = {"--points1"}, help = "Points scored by the first team.") int team1Points,
                                        @ShellOption(value = {"--team2"}, help = "Name of the second team.") String team2Name,
                                        @ShellOption(value = {"--points2"}, help = "Points scored by the second team.")int team2Points,
                                        @ShellOption(value = {"--tournament"}, help = "Name of the tournament.") String tournamentName) {
        try {
            Tournament tournament = tournamentService.getTournament(tournamentName);
            tournamentService.scoreGameInTournament(team1Name, team1Points, team2Name, team2Points, tournament);
            return String.format("Score for game in tournament '%s' set: team '%s' scored %d point(s) and team '%s' scored %d point(s)", tournamentName, team1Name, team1Points, team2Name, team2Points);
        } catch (TournamentDoesNotExistException e) {
            return String.format("Tournament with name '%s' does not exist", tournamentName);
        } catch (TeamDoesNotExistException e) {
            return String.format("Score for game in tournament '%s' NOT set because either team '%s' or team '%s' does not exist", tournamentName, team1Name, team2Name);
        } catch (GameDoesNotExistException e) {
            return String.format("Score for game between team '%s' and team '%s' NOT set because that game does not exist in tournament '%s'", team1Name, team2Name, tournamentName);
        } catch (InvalidScoreException e) {
            return String.format("Score for game in tournament '%s' NOT set because the score is invalid: team '%s' scored %d point(s) and team '%s' scored %d point(s)", tournamentName, team1Name, team1Points, team2Name, team2Points);
        }        
    }

    @ShellMethod(value = "Report game results for tournament.", group = "Tournament Commands")
    public String reportTournamentResults(@ShellOption(value = {"--tournament"}, help = "Name of the tournament.") String tournamentName) throws TournamentDoesNotExistException {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("teamName", "Team Name");
        headers.put("wins", "W");
        headers.put("losses", "L");
        headers.put("ties", "T");
        headers.put("total", "TOTAL");
        Tournament tournament = tournamentService.getTournament(tournamentName);
        List<Tally> tallies = tournament.getTally();
        logger.debug("Tallies: " + tallies);
        TableModel model = new BeanListTableModel<>(tallies, headers);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        return tableBuilder.build().render(100);
    }
}
