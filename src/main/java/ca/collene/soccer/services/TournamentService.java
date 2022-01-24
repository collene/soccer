package ca.collene.soccer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import ca.collene.soccer.entities.Team;
import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.repositories.TournamentRepository;

@Service
public class TournamentService {
    //private Logger logger = LoggerFactory.getLogger(TournamentService.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamService teamService;

    public Tournament createTournament(String name) throws NameAlreadyExistsException {        
        Tournament newTournament = new Tournament.With().name(name)
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
        tournamentRepository.save(tournament);
        return team;
    }

    public void addGameToTournament(String team1Name, String team2Name, Tournament tournament) throws GameAlreadyInTournamentException {
        Team team1 = ensureTeamInTournament(team1Name, tournament);
        Team team2 = ensureTeamInTournament(team2Name, tournament);
        if(tournament.hasGameWithTeams(team1, team2)) {
            throw new GameAlreadyInTournamentException("The game between teams " + team1Name + " and " + team2Name + " already exists in tournament " + tournament.getName());
        }        
        tournament.addGame(team1, team2);    
        tournamentRepository.save(tournament);    
    }
}
