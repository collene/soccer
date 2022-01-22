package ca.collene.soccer.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import ca.collene.soccer.entities.Team;
import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.repositories.TournamentRepository;

@Service
public class TournamentService {
    private Logger logger = LoggerFactory.getLogger(TournamentService.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamService teamService;

    public Tournament createTournament(String name) throws NameAlreadyExistsException {
        logger.debug("Creating tournament with name: " + name);
        Tournament newTournament = new Tournament(name);
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

    public void addTeamToTournament(String teamName, String tournamentName) throws TournamentDoesNotExistException, TeamAlreadyInTournamentException {
        Tournament tournament = getTournament(tournamentName);
        Team team = null;
        try {
            team = teamService.getTeam(teamName);
        } catch(TeamDoesNotExistException e) {
            try {
                team = teamService.createTeam(teamName);
            } catch(NameAlreadyExistsException e2) {
                // this "shouldn't" happen :D
                throw new RuntimeException("Invalid data state encountered", e2);
            }            
        }
        if(tournament.hasTeam(team)) {
            throw new TeamAlreadyInTournamentException("Team " + teamName + " is already in tournament " + tournamentName);
        }
        tournament.addTeam(team);
        tournamentRepository.save(tournament);
    }
}
