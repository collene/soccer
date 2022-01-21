package ca.collene.soccer.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.repositories.TournamentRepository;

@Service
public class TournamentService {
    private Logger logger = LoggerFactory.getLogger(TournamentService.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    public Tournament createTournament(String name) throws TournamentNameAlreadyExistsException {
        logger.debug("Creating tournament with name: " + name);
        Tournament newTournament = new Tournament(name);
        try {
            return tournamentRepository.save(newTournament);
        } catch(DataIntegrityViolationException e) {
            throw new TournamentNameAlreadyExistsException("A tournament with name " + name + " already exists");
        }
        
    }

    public Tournament getTournament(String name) {
        return tournamentRepository.findByName(name);
    }
}
