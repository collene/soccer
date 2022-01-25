package ca.collene.soccer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import ca.collene.soccer.entities.Team;
import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.repositories.TournamentRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TournamentService {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamService teamService;

    public Tournament createTournament(String name) throws NameAlreadyExistsException {        
        Tournament newTournament = Tournament.builder().name(name)
                                        .build();
        try {
            return tournamentRepository.save(newTournament);
        } catch(DataIntegrityViolationException e) {
            throw new NameAlreadyExistsException("A tournament with name " + name + " already exists");
        }        
    }

    public Tournament getTournament(String name) throws TournamentDoesNotExistException {
        Tournament tournament = tournamentRepository.findByName(name);
        if(tournament == null) {
            throw new TournamentDoesNotExistException("Tournament with name " + name + " was not found");
        }
        return tournament;
    }

    public void addTeamToTournament(String teamName, Tournament tournament) throws TeamAlreadyInTournamentException {        
        Team team = teamService.getOrCreateTeam(teamName);
        if(tournament.hasTeam(team)) {
            throw new TeamAlreadyInTournamentException("Team " + teamName + " is already in tournament " + tournament.getName());
        }
        tournament.addTeam(team);
        tournamentRepository.save(tournament);
    }
    private Team ensureTeamInTournament(String teamName, Tournament tournament) {
        Team team = teamService.getOrCreateTeam(teamName);
        if(!tournament.hasTeam(team)) {
            tournament.addTeam(team);
        }        
        return team;
    }

    public void addGameToTournament(String team1Name, String team2Name, Tournament tournament) throws GameAlreadyInTournamentException, InvalidGameException {        
        log.debug("Ensuring teams in tournament. Number teams before: " + tournament.getTeams().size());
        Team team1 = ensureTeamInTournament(team1Name, tournament);
        Team team2 = ensureTeamInTournament(team2Name, tournament);
        log.debug("End of ensuring teams in tournament.  Number teams after: " + tournament.getTeams().size());
        if(team1.equals(team2)) {
            throw new InvalidGameException("The team " + team1Name + " can not play itself in tournament " + tournament);
        }
        if(tournament.hasGameWithTeams(team1, team2)) {
            throw new GameAlreadyInTournamentException("The game between teams " + team1Name + " and " + team2Name + " already exists in tournament " + tournament.getName());
        }        
        tournament.addGame(team1, team2);    
        tournamentRepository.save(tournament);    
    }

    public void scoreGameInTournament(String team1Name, int team1Points, String team2Name, int team2Points, Tournament tournament) throws TeamDoesNotExistException, GameDoesNotExistException, InvalidScoreException {
        Team team1 = teamService.getTeam(team1Name);
        Team team2 = teamService.getTeam(team2Name);
        tournament.scoreGame(team1, team1Points, team2, team2Points);
        tournamentRepository.save(tournament);
    }
}
