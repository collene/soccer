package ca.collene.soccer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import ca.collene.soccer.entities.Team;
import ca.collene.soccer.repositories.TeamRepository;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    public Team createTeam(String name) throws NameAlreadyExistsException {
        Team newTeam = new Team(name);
        try {
            return teamRepository.save(newTeam);
        } catch(DataIntegrityViolationException e) {
            throw new NameAlreadyExistsException("A team with name " + name + " already exists");
        }
    }

    public Team getTeam(String name) throws TeamDoesNotExistException {
        Team team = teamRepository.findByName(name);
        if(team == null) {
            throw new TeamDoesNotExistException("Team with name " + name + " was not found");
        }
        return team;
    }
}
