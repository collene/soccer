package ca.collene.soccer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.collene.soccer.entities.Tournament;
import ca.collene.soccer.repositories.TournamentRepository;

@Service
public class TournamentService {
    @Autowired
    private TournamentRepository tournamentRepository;

    public Tournament createTournament(String name) {
        Tournament newTournament = new Tournament(name);
        // TODO:  check for duplicate name
        return tournamentRepository.save(newTournament);
    }

    public Tournament getTournament(String name) {
        return tournamentRepository.findByName(name);
    }
}
